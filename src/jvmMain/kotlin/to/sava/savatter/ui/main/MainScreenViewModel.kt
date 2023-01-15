package to.sava.savatter.ui.main

import com.squareup.sqldelight.db.SqlDriver
import to.sava.savatter.database.Storage
import to.sava.savatter.viewmodels.ViewModelBase

class MainScreenViewModel(
    private val storageDriver: SqlDriver,
    private val storage: Storage,
) : ViewModelBase() {

}
