package to.sava.savatter.ui.root

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import app.softwork.routingcompose.DesktopRouter
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigateBack

@Composable
fun RootWindow(
    onCloseRequest: () -> Unit,
    viewModel: RootWindowViewModel,
    modifier: Modifier = Modifier,
) {
    viewModel.bindScope(rememberCoroutineScope())
    val state = rememberWindowState()
    val localDensity = LocalDensity.current

    var windowToBeResized by remember { mutableStateOf(true) }
    if (windowToBeResized) {
        viewModel.loadWindowPositionsToWindowState(localDensity, state)
        windowToBeResized = false
    }

    Window(
        onCloseRequest = {
            viewModel.saveWindowPositions(localDensity, state)
            onCloseRequest()
        },
        state = state,
    ) {
        MaterialTheme {
            DesktopRouter("/") {
                val router = Router.current
                route("/") {
                    Column {
                        Button(
                            onClick = { router.navigate("/settings") }
                        ) {
                            Text("settings")
                        }
                        Text("x: ${state.position.x}")
                        Text("y: ${state.position.y}")
                        Text("w: ${state.size.width}")
                        Text("h: ${state.size.height}")
                    }
                }
                route("/settings") {
                    Button(
                        onClick = { router.navigateBack() }
                    ) {
                        Text("←")
                    }
                }
            }
        }
    }
}
