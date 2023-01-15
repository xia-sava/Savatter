package to.sava.savatter.repositories

import to.sava.savatter.database.Storage

enum class SettingsKey(val key: String, val comment: String) {
    WINDOW_POS_X("window_pos_x", "ウィンドウ座標 X"),
    WINDOW_POS_Y("window_pos_y", "ウィンドウ座標 Y"),
    WINDOW_WIDTH("window_width", "ウィンドウ幅"),
    WINDOW_HEIGHT("window_height", "ウィンドウ高さ"),
}

class SettingsRepository(
    private val storage: Storage
) {
    fun read(key: SettingsKey): String =
        storage.settingsQueries.selectValue(key.key).executeAsOne()

    fun write(key: SettingsKey, value: String) {
        storage.settingsQueries.insert(key.key, value, key.comment)
    }

    fun readInt(key: SettingsKey): Int =
        storage.settingsQueries.selectValue(key.key).executeAsOne().toInt()

    fun writeInt(key: SettingsKey, value: Int) {
        storage.settingsQueries.insert(key.key, value.toString(), key.comment)
    }

    fun readFloat(key: SettingsKey): Float =
        storage.settingsQueries.selectValue(key.key).executeAsOne().toFloat()

    fun writeFloat(key: SettingsKey, value: Float) {
        storage.settingsQueries.insert(key.key, value.toString(), key.comment)
    }

    fun readBoolean(key: SettingsKey): Boolean =
        storage.settingsQueries.selectValue(key.key).executeAsOne().toBoolean()

    fun writeBoolean(key: SettingsKey, value: Boolean) {
        storage.settingsQueries.insert(key.key, value.toString(), key.comment)
    }
}
