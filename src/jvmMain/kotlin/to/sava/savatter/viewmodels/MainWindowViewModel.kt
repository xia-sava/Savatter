package to.sava.savatter.viewmodels

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.toURI
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.*
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import to.sava.savatter.config.BuildKonfig
import to.sava.savatter.data.TextTest
import to.sava.savatter.database.Storage
import twitter4j.OAuthAuthorization
import twitter4j.RequestToken
import java.awt.Desktop
import java.net.URI
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random.Default.nextBytes
import io.ktor.client.engine.cio.CIO as clientCIO
import io.ktor.server.cio.CIO as serverCIO


class MainWindowViewModel(
    private val storageDriver: SqlDriver,
    private val storage: Storage,
) : ViewModelBase() {

    private var _oAuthProgress = MutableStateFlow(false)
    val oAuthProgress = _oAuthProgress.asStateFlow()

    private val queries = storage.textTestQueries

    private val twitterOAuth = OAuthAuthorization.newBuilder()
        .prettyDebugEnabled(true)
        .oAuthConsumer(BuildKonfig.twitterConsumerKey, BuildKonfig.twitterConsumerSecret)
        .build()
    private var oAuthRequestToken: RequestToken? = null
    val twitterUserId = MutableStateFlow(0L)
    val twitterScreenName = MutableStateFlow("screen name")
    val twitterAccessToken = MutableStateFlow("access token")
    val twitterAccessTokenSecret = MutableStateFlow("access token secret")

    fun twitterOAuth() {
        _oAuthProgress.value = true
        viewModelScope.launch(Dispatchers.IO) {

            twitterOAuth.getOAuthRequestToken("oob").let {
                oAuthRequestToken = it
                Desktop.getDesktop().browse(URI(it.authorizationURL))
                _oAuthProgress.value = false
            }
        }
    }

    fun twitterOAuthPIN(PIN: String) {
        viewModelScope.launch(Dispatchers.IO) {
            twitterOAuth.getOAuthAccessToken(PIN)?.let {
                twitterUserId.value = it.userId
                twitterScreenName.value = it.screenName
                twitterAccessToken.value = it.token
                twitterAccessTokenSecret.value = it.tokenSecret
            }
        }
    }

    fun count(): Flow<Long> {
        return queries.count().asFlow().mapToOne(Dispatchers.IO)
    }

    fun insert(text1: String, text2: String) {
        queries.insert(text1, text2)
    }

    fun selectAll(): Flow<List<TextTest>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.IO)
    }

    fun twitterOAuth2() {
        _oAuthProgress.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val oauth2CallbackServer =
                embeddedServer(serverCIO, port = 56764, module = Application::oauth2CallbackServer)
            val codeVerify = nextBytes(32).encodeUrlSafeBase64()
            val codeChallenge =
                MessageDigest.getInstance("SHA-256").digest(codeVerify.toByteArray())
                    .encodeUrlSafeBase64()
            val state = nextBytes(80)
            val redirectUri = "http://127.0.0.1:56764/oauth2_callback"
            Desktop.getDesktop().browse(
                URLBuilder("https://twitter.com/i/oauth2/authorize").apply {
                    parameters.apply {
                        append("response_type", "code")
                        append("client_id", BuildKonfig.twitterClientId)
                        append("redirect_uri", redirectUri)
                        append("state", state.encodeUrlSafeBase64())
                        append("scope", "tweet.read tweet.write users.read offline.access")
                        append("code_challenge", codeChallenge)
                        append("code_challenge_method", "S256")
                    }
                }.build().toURI()
            )
            oauth2CallbackServer.start()
            val job = viewModelScope.launch(Dispatchers.IO) {
                val (rState, code) = oauth2CallbackServerChannel.receive()
                if (rState == state.encodeUrlSafeBase64() && code != null) {
                    val client = HttpClient(clientCIO) {
                        install(ContentNegotiation) {
                            json(json = Json {
                                isLenient = false
                                ignoreUnknownKeys = true
                                allowSpecialFloatingPointValues = true
                                useArrayPolymorphism = false
                            })
                        }
                    }
                    val response: HttpResponse = client.submitForm(
                        url = "https://api.twitter.com/2/oauth2/token",
                        formParameters = Parameters.build {
                            append("code", code)
                            append("grant_type", "authorization_code")
                            append("client_id", BuildKonfig.twitterClientId)
                            append("redirect_uri", redirectUri)
                            append("code_verifier", codeVerify)
                        }
                    )
                    val token = response.body<TokenCallback>()
                }
            }
            job.invokeOnCompletion {
                oauth2CallbackServer.stop()
            }
        }
    }

    companion object {
        val oauth2CallbackServerChannel = Channel<OAuth2Callback>()
    }
}

data class OAuth2Callback(
    val state: String?,
    val code: String?,
)

@Serializable
data class TokenCallback(
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("access_token") val accessToken: String,
    @SerialName("scope") val scope: String,
    @SerialName("refresh_token") val refreshToken: String,
)

fun Application.oauth2CallbackServer() {
    routing {
        get("/oauth2_callback") {
            val params = call.request.queryParameters

            MainWindowViewModel.oauth2CallbackServerChannel.send(
                OAuth2Callback(params["state"], params["code"])
            )

            call.respondText(
                buildString {
                    appendHTML().html {
                        body {
                            button {
                                text("close")
                                onClick = "alert(1); window.close()"
                            }
                        }
                    }
                },
                ContentType.Text.Html,
            )
        }
    }
}

fun ByteArray.encodeUrlSafeBase64(): String =
    Base64.getUrlEncoder().withoutPadding().encodeToString(this)
