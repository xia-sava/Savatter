package to.sava.savatter.di

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import net.harawata.appdirs.AppDirsFactory
import org.koin.dsl.module
import to.sava.savatter.database.Storage
import to.sava.savatter.repositories.SettingsRepository
import to.sava.savatter.ui.main.MainScreenViewModel
import to.sava.savatter.ui.settings.SettingsScreenViewModel
import to.sava.savatter.ui.root.RootWindowViewModel
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.exists


fun appModule() = module {

    val dbFile = Paths.get(
        AppDirsFactory.getInstance()
            .getUserDataDir("Savatter", "", "Sava"),
        "savatter.sqlite"
    )

    single<SqlDriver> {
        JdbcSqliteDriver("jdbc:sqlite:${dbFile}")
    }
    single {
        if (!dbFile.exists()) {
            Files.createDirectories(dbFile.parent)
            Storage.Schema.create(get())
        }
        Storage(get())
    }
    factory { SettingsRepository(get()) }
    factory { RootWindowViewModel(get()) }
    factory { MainScreenViewModel(get(), get()) }
    factory { SettingsScreenViewModel(get(), get()) }
}
