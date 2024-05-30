import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.reflect.KProperty

@Composable
fun NavLayout(items: List<NavScreen<*>>) {
    var selectedItem by remember { mutableStateOf(items.first().name) }
    items.forEach { it.nav = { screen -> selectedItem = screen.name } }
    Scaffold(
        content = {
            items.first { it.name == selectedItem }.apply { screen(this) }
        },
        bottomBar = {
            NavigationBar {
                items.map {
                    NavigationBarItem(
                        icon = { Icon(it.icon, it.name) },
                        label = { Text(it.name) },
                        onClick = { selectedItem = it.name },
                        selected = selectedItem == it.name
                    )
                }
            }
        }
    )
}

data class NavScreen<T>(val name: String, val icon: ImageVector, var prop: T, private val _screen: @Composable (NavScreen<T>) -> Unit) where T: Any {
    @Suppress("UNCHECKED_CAST")
    val screen = @Composable { nav: NavScreen<*> -> (nav as? NavScreen<T>)?.let { _screen(this) } }

    lateinit var nav: (NavScreen<*>) -> Unit
    operator fun invoke(para : T): NavScreen<T> {
        prop = para
        return this
    }
}