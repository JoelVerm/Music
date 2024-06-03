import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.flami.music.AppTheme
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
        val playlists by getDownloadedSongs()
        if (playlists == null) {
            Scaffold {
                Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Text("Grant storage permission")
                    Text("to access music files.")
                }
            }
        } else {
            NavLayout(
                listOf(
                    if(playlists!!.any { it.songs.isNotEmpty() }) PlayScreen(
                        Pair(
                            playlists!!.first(),
                            playlists!!.first().songs.first()
                        )
                    ) else PlayScreen,
                    SearchScreen(5),
                    PlaylistsScreen(playlists!!),
                )
            )
        }
    }
}