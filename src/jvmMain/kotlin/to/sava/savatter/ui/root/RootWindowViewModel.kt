package to.sava.savatter.ui.root

import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import to.sava.savatter.repositories.SettingsKey
import to.sava.savatter.repositories.SettingsRepository
import to.sava.savatter.viewmodels.ViewModelBase

class RootWindowViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModelBase() {

    fun loadWindowPositionsToWindowState(density: Density, state: WindowState) {
        val x: Float
        val y: Float
        val w: Float
        val h: Float
        try {
            x = settingsRepository.readFloat(SettingsKey.WINDOW_POS_X)
            y = settingsRepository.readFloat(SettingsKey.WINDOW_POS_Y)
            w = settingsRepository.readFloat(SettingsKey.WINDOW_WIDTH)
            h = settingsRepository.readFloat(SettingsKey.WINDOW_HEIGHT)
        } catch (e: NullPointerException) {
            return
        }
        with(density) {
            state.position = WindowPosition.Absolute(x.toDp(), y.toDp())
            state.size = state.size.copy(w.toDp(), h.toDp())
        }
    }

    fun saveWindowPositions(density: Density, state: WindowState) {
        with(density) {
            settingsRepository.writeFloat(SettingsKey.WINDOW_POS_X, state.position.x.toPx())
            settingsRepository.writeFloat(SettingsKey.WINDOW_POS_Y, state.position.y.toPx())
            settingsRepository.writeFloat(SettingsKey.WINDOW_WIDTH, state.size.width.toPx())
            settingsRepository.writeFloat(SettingsKey.WINDOW_HEIGHT, state.size.height.toPx())
        }
    }
}
