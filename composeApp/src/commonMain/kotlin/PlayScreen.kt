import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

val PlayScreen = NavScreen("Play", Icons.Rounded.PlayArrow,
    Playlist("") to Song("", "", 0, "", ImageBitmap(5, 5))) {
    val player by player()

    var queue: MutableList<Song> by remember { mutableStateOf(mutableListOf()) }
    val pastQueue: MutableList<Song> by remember { mutableStateOf(mutableListOf()) }

    val playlist by rememberDerived(it.prop.first) {
        val index = it.prop.first.songs.indexOf(it.prop.second)
        queue = it.prop.first.songs.drop(index + 1).toMutableList()
        pastQueue.clear()
        it.prop.first
    }
    val song by rememberDerived(it.prop.second) {
        player.load(it.prop.second)
        it.prop.second
    }

    var playing by rememberWith(false) { playing -> player.playing(playing) }

    var songProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while(true) {
            songProgress = player.progress().toFloat()
            delay(500)
        }
    }

    var shuffle by rememberWith(playlist, false) {
        shuffle -> if (shuffle) queue = queue.shuffled().toMutableList()
    }
    var repeat by remember(playlist) { mutableStateOf(false) }

    var reloadThingy by remember { mutableStateOf(0) }
    fun nextSong() {
        if (queue.isEmpty()) {
            if (repeat) {
                queue = if (shuffle) playlist.songs.shuffled().toMutableList()
                    else playlist.songs.toMutableList()
                pastQueue.clear()
            }
            else {
                playing = false
                return
            }
        }
        pastQueue.add(song)
        val nextSong = queue.removeFirst()
        it(it.prop.first to nextSong)
        reloadThingy++
    }
    fun previousSong() {
        if (pastQueue.isEmpty()) {
            player.seekTo(0)
            return
        }
        queue.add(0, song)
        val previousSong = pastQueue.removeLast()
        it(it.prop.first to previousSong)
        reloadThingy++
    }

    player.onComplete { nextSong() }

    Column(
        modifier = Modifier.fillMaxWidth().weight(1f).padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(bitmap = song.cover,
            contentDescription = "Album art",
            modifier = Modifier.width(350.dp).aspectRatio(1f).clip(RoundedCornerShape(25.dp)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Column(
            modifier = Modifier.padding(0.dp, 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(playlist.name, style = MaterialTheme.typography.titleMedium)
            Text(song.name, style = MaterialTheme.typography.displaySmall)
            Text(song.artist, style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val transparentColor = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                disabledContainerColor = Color.Transparent
            )
            val secondaryColor = ButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
            Button(
                content = { Icon(Icons.Rounded.Shuffle, "Shuffle") },
                onClick = { shuffle = !shuffle },
                colors = if (shuffle) secondaryColor else transparentColor,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Button(
                content = { Icon(Icons.Rounded.SkipPrevious, "Previous") },
                onClick = { previousSong() },
                colors = transparentColor,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Button(
                content = { if (playing)
                    Icon(Icons.Rounded.Pause, "Pause")
                    else Icon(Icons.Rounded.PlayArrow, "Play")
                },
                onClick = { playing = !playing },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Button(
                content = { Icon(Icons.Rounded.SkipNext, "Next") },
                onClick = { nextSong() },
                colors = transparentColor,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Button(
                content = { Icon(Icons.Rounded.Repeat, "Repeat") },
                onClick = { repeat = !repeat },
                colors = if (repeat) secondaryColor else transparentColor,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
        Slider(
            value = songProgress,
            valueRange = 0f..song.duration.toFloat(),
            onValueChange = { value ->
                player.seekTo(value.toInt())
                songProgress = value
            },
            modifier = Modifier.fillMaxWidth().padding(0.dp, 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(songProgress.toInt().timeStamp(), style = MaterialTheme.typography.bodyMedium)
            Text(song.duration.timeStamp(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
