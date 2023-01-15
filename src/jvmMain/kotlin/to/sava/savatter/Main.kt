package to.sava.savatter

import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import to.sava.savatter.di.appModule
import to.sava.savatter.di.getF
import to.sava.savatter.ui.root.RootWindow

fun main() = application {
    startKoin {
        modules(appModule())
    }

    RootWindow(
        onCloseRequest = ::exitApplication,
        getF(),
    )
}
