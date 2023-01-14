package to.sava.savatter.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import to.sava.savatter.viewmodels.MainWindowViewModel

@Composable
fun MainWindow(
    viewModel: MainWindowViewModel,
    modifier: Modifier = Modifier,
) {
    viewModel.bindScope(rememberCoroutineScope())

    var info by remember { mutableStateOf("???") }
    val count by viewModel.count().collectAsState(-1)
    val items by viewModel.selectAll().collectAsState(listOf())

    val oAuthProgress by viewModel.oAuthProgress.collectAsState(false)
    val fetchProgress by viewModel.fetchProgress.collectAsState(false)
    var oAuthPIN by remember { mutableStateOf("") }
    val twitterUserId by viewModel.twitterUserId.collectAsState(0L)
    val twitterScreenName by viewModel.twitterScreenName.collectAsState("screen name")
    val twitterAccessToken by viewModel.twitterAccessToken.collectAsState("access token")
    val twitterAccessTokenSecret by viewModel.twitterAccessTokenSecret.collectAsState("access token secret")
    val twitterPersonalToken by viewModel.twitterPersonalToken.collectAsState("personal token")

    val twitterTimeline by viewModel.twitterTimeline.collectAsState(listOf("--empty--"))

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                info = "Clicked"
                viewModel.twitterOAuth()
            },
        ) {
            if (oAuthProgress) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
            } else {
                Text("OAuth")
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            TextField(
                value = oAuthPIN,
                onValueChange = {
                    oAuthPIN = it
                },
                label = { Text("PIN") },
            )
            Button(
                onClick = {
                    viewModel.twitterOAuthPIN(oAuthPIN)
                },
            ) {
                Text("Submit PIN")
            }
        }
        Row {
            Text(
                if (twitterUserId == 0L) "user id" else twitterUserId.toString(),
                modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                twitterScreenName,
                modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                twitterAccessToken,
                modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                twitterAccessTokenSecret,
                modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.Black)

        Column {
            Button(
                onClick = {
                    viewModel.twitterOAuth2()
                },
            ) {
                if (oAuthProgress) {
                    CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
                } else {
                    Text("OAuth2")
                }
            }
            Spacer(Modifier.width(4.dp))
            Text(
                twitterPersonalToken,
                modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            )
            Spacer(Modifier.width(4.dp))
            Button(
                onClick = {
                    viewModel.twitterFetchTimeline()
                },
            ) {
                if (fetchProgress) {
                    CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
                } else {
                    Text("fetch Timeline")
                }
            }
            Spacer(Modifier.width(4.dp))
            LazyColumn {
                items(twitterTimeline) {
                    Text(text = it)
                    Divider(color = Color.Gray)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Divider(color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            text = count.toString(),
        )
        LazyColumn {
            items(items) {
                Text(text = "${it.text1} / ${it.text2}")
                Divider(color = Color.Gray)
            }
        }
    }
}

