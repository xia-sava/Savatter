package to.sava.savatter.ui.settings

import com.squareup.sqldelight.db.SqlDriver
import to.sava.savatter.database.Storage
import to.sava.savatter.viewmodels.ViewModelBase

class SettingsScreenViewModel(
    private val storageDriver: SqlDriver,
    private val storage: Storage,
) : ViewModelBase() {

}
