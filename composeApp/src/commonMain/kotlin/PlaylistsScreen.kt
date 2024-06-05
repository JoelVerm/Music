import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

val PlaylistsScreen = NavScreen("Playlists", Icons.AutoMirrored.Rounded.PlaylistPlay, listOf<Playlist>()) {
    Column(
        modifier = Modifier.fillMaxWidth().weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        it.prop.map { playlist ->
            Card(
                modifier = Modifier.padding(10.dp).fillMaxWidth().clip(
                    RoundedCornerShape(20.dp)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                ) {
                    var songsVisible by remember { mutableStateOf(false) }
                    Text(
                        playlist.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clip(
                            RoundedCornerShape(15.dp)
                        ).fillMaxWidth().clickable { songsVisible = !songsVisible }
                    )
                    AnimatedVisibility(songsVisible) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.height(0.dp))
                            playlist.songs.map { song ->
                                ListItem(
                                    headlineContent = { Text(song.name) },
                                    overlineContent = { Text(song.artist) },
                                    supportingContent = { Text(song.duration.timeStamp()) },
                                    leadingContent = {
                                        Image(
                                            song.cover,
                                            "Album art",
                                            modifier = Modifier.height(50.dp).aspectRatio(1f).clip(
                                                RoundedCornerShape(10.dp)
                                            ),
                                            contentScale = ContentScale.Crop,
                                            alignment = Alignment.Center
                                        )
                                    },
                                    trailingContent = {
                                        Button(
                                            onClick = { it.nav(PlayScreen(Pair(playlist, song))) },
                                            content = { Text("Play") }
                                        )
                                    },
                                    modifier = Modifier.clip(
                                        RoundedCornerShape(15.dp)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
