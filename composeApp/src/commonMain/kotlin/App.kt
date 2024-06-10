import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
        darkTheme: Boolean = true,
        dynamicColor: Boolean = false
    ) {
    AppTheme(
        darkTheme,
        dynamicColor
    ) {
        val permissionsGranted by requestPermissions()
        if (!permissionsGranted) {
            Scaffold {
                Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Text("Grant all permissions")
                    Text("to play music")
                }
            }
        } else {
            var playlists by remember { mutableStateOf(null as List<Playlist>?) }
            if (playlists == null)
                playlists = getDownloadedSongs()
            val lastSong = getLastSongProgress()

            NavLayout(
                listOf(
                    PlayScreen(
                        lastSong?.let { PlayState(playlists!!, it.playlist, it.song, it.progress) } ?: PlayState(playlists!!)
                    ),
                    SearchScreen(5),
                    PlaylistsScreen(playlists!!),
                )
            )
        }
    }
}

@Composable
expect fun AppTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
)