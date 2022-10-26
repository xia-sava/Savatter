package to.sava.savatter.viewmodels

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import to.sava.savatter.config.BuildKonfig
import to.sava.savatter.data.TextTest
import to.sava.savatter.database.Storage
import twitter4j.OAuthAuthorization
import twitter4j.RequestToken
import java.awt.Desktop
import java.net.URI

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
}
