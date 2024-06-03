import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun NavLayout(items: List<NavScreen<*>>) {
    var selectedItem by remember { mutableStateOf(0) }
    val navWidth = windowWidth() * items.size
    val slideItem by animateFloatAsState((selectedItem.toFloat() - (items.size / 2)) * -windowWidth())
    items.forEach { it.nav = { screen -> selectedItem = items.indexOf(screen) } }
    Scaffold(
        content = {
            Box(
                modifier = Modifier.fillMaxWidth().consumeWindowInsets(it).padding(it)
            ) {
                Row(
                    modifier = Modifier.requiredWidth(navWidth.dp)
                        .absoluteOffset(x = slideItem.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items.map {  it.screen(this, it) }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                items.withIndex().map {
                    NavigationBarItem(
                        icon = { Icon(it.value.icon, it.value.name) },
                        label = { Text(it.value.name) },
                        onClick = { selectedItem = it.index },
                        selected = selectedItem == it.index
                    )
                }
            }
        }
    )
}

data class NavScreen<T>(val name: String, val icon: ImageVector, var prop: T, private val _screen: @Composable RowScope.(NavScreen<T>) -> Unit) where T: Any {
    @Suppress("UNCHECKED_CAST")
    val screen: @Composable RowScope.(NavScreen<*>) -> Unit? = @Composable { _screen(it as NavScreen<T>) }

    lateinit var nav: (NavScreen<*>) -> Unit
    operator fun invoke(para : T): NavScreen<T> {
        prop = para
        return this
    }
}

@Composable
expect fun windowWidth(): Int