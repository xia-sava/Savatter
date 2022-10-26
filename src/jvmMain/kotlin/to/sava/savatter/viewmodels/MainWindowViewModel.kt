package to.sava.savatter.viewmodels

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import to.sava.savatter.data.TextTest
import to.sava.savatter.database.Storage

class MainWindowViewModel(
    private val storageDriver: SqlDriver,
    private val storage: Storage,
) : ViewModelBase() {

    private val queries = storage.textTestQueries

    init {
    }

    fun twitterOAuth() {
    }


    fun count(): Flow<Long> {
        return queries.count().asFlow().mapToOne(Dispatchers.IO)
    }

    fun insert(text1: String, text2: String) {
        queries.insert(text1, text2)
    }

    fun selectAll(): Flow<List<TextTest>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.IO)
    }
}
