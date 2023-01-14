package to.sava.savatter

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import to.sava.savatter.di.appModule
import to.sava.savatter.di.getF
import to.sava.savatter.ui.StudyWindow

fun main() = application {
    startKoin {
        modules(appModule())
    }

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
fun App() {
    MaterialTheme {
        StudyWindow(getF())
    }
}
