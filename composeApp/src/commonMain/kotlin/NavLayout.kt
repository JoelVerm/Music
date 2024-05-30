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

@Composable
fun NavLayout(items: List<NavItem>) {
    var selectedItem by remember { mutableStateOf(items.first().name) }
    Scaffold(
        content = {
            items.first { it.name == selectedItem }.screen()
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

data class NavItem(val name: String, val icon: ImageVector, val screen: @Composable () -> Unit)
