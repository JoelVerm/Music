import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

val SearchScreen = NavScreen("Search", Icons.Rounded.Search, 0) {
    Column(
        modifier = Modifier.fillMaxWidth().weight(1f).background(color = Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${it.name} with text: ${it.prop}")
        Button(onClick = { it.nav(PlayScreen(Pair("Pancakes", "Sugar"))) }) {
            Text("Click me")
        }
    }
}
