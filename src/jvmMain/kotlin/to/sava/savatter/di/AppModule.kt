package to.sava.savatter.di

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import org.koin.dsl.module
import to.sava.savatter.database.Storage
import to.sava.savatter.viewmodels.MainWindowViewModel


fun appModule() = module {
    single<SqlDriver> { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY) }
    single {
        Storage.Schema.create(get())
        Storage(get())
    }
    factory { MainWindowViewModel(get(), get()) }
}
