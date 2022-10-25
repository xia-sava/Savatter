import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import to.sava.savatter.Storage
import to.sava.savatter.data.TextTest

@Composable
fun MainWindow(
    viewModel: MainWindowViewModel,
    modifier: Modifier = Modifier,
) {
    var info by remember { mutableStateOf("???") }
    val count by viewModel.count().collectAsState(-1)
    val items by viewModel.selectAll().collectAsState(listOf())

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                info = "Clicked"
                viewModel.insert("text1 $count", "text2 $count")
            },
        ) {
            Text("OAuth2")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.border(1.dp, Color.Black).padding(8.dp),
            text = count.toString(),
        )
        LazyColumn() {
            items(items) {
                Text(text = "${it.text1} / ${it.text2}")
                Divider(color = Color.Gray)
            }
        }
    }
}

class MainWindowViewModel(val scope: CoroutineScope) {
    val context = scope.coroutineContext
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    val queries = Storage(driver).textTestQueries

    init {
        Storage.Schema.create(driver)
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
