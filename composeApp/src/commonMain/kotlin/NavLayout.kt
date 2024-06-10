import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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

const val minDragDelta = 100

@Composable
fun NavLayout(items: List<NavScreen<*>>) {
    var selectedItem by remember { mutableStateOf(0) }
    val slideItem by animateFloatAsState((items.size / 2) - selectedItem.toFloat())
    items.forEach { it.nav = { selectedItem = items.indexOf(it) } }
    Scaffold(
        content = {
            var draggableDelta by remember { mutableStateOf(0f) }
            BoxWithConstraints (
                modifier = Modifier.fillMaxWidth().consumeWindowInsets(it).padding(it)
                    .draggable(orientation = Orientation.Horizontal, state = rememberDraggableState { delta ->
                        draggableDelta += delta
                    }, onDragStarted = { draggableDelta = 0f }, onDragStopped = {
                        if (draggableDelta > minDragDelta)
                            selectedItem = (selectedItem - 1).coerceAtLeast(0)
                        else if (draggableDelta < -minDragDelta)
                            selectedItem = (selectedItem + 1).coerceAtMost(items.size - 1)
                    })
            ) {
                Row(
                    modifier = Modifier.requiredWidth(maxWidth * items.size)
                        .absoluteOffset(x = maxWidth * slideItem),
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

    lateinit var nav: () -> Unit
    operator fun invoke(para : T): NavScreen<T> {
        prop = para
        return this
    }
}