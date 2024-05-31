import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

val PlaylistsScreen = NavScreen("Playlists", Icons.AutoMirrored.Rounded.PlaylistPlay, "Default") {
    Column(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${it.name} with text: ${it.prop}")
    }
}
