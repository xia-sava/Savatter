package to.sava.savatter.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
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
            Text("OAuth2")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            text = count.toString(),
        )
        LazyColumn() {
            items(items) {
                Text(text = "${it.text1} / ${it.text2}")
                Divider(color = Color.Gray)
            }
        }
    }
}

