import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

val SearchScreen = NavScreen("Search", Icons.Rounded.Search, 0) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${it.name} with text: ${it.prop}")
        Button(onClick = { it.nav(HomeScreen("Pancakes")) }) {
            Text("Click me")
        }
    }
}
