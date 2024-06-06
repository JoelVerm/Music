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
    Playlist("") to Song("", "", 0, "", ImageBitmap(5, 5)) to 0) {
    val player by player()

    val playlist by rememberDerived(it.prop.playlist) {
        player.load(it.prop.playlist)
        it.prop.playlist
    }
    val _song by rememberDerived(it.prop.song) {
        player.goto(it.prop.song)
        it.prop.song
    }
    val _start by rememberDerived(it.prop.startTime) {
        player.seekTo(it.prop.startTime)
        it.prop.startTime
    }

    var currentSong by remember { mutableStateOf(player.currentSong()) }
    var songProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while(true) {
            currentSong = player.currentSong()
            songProgress = player.progress().toFloat()
            delay(500)
        }
    }

    var playing by rememberWith(player.playing()) { playing -> player.playing(playing) }

    var shuffle by rememberWith(playlist, false) {
        shuffle -> player.shuffle(shuffle)
    }
    var repeat by rememberWith(playlist, false) {
        repeat -> player.repeat(repeat)
    }

    fun nextSong() {
        player.next()
    }
    fun previousSong() {
        player.previous()
    }

    Column(
        modifier = Modifier.fillMaxWidth().weight(1f).padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(bitmap = currentSong?.cover ?: ImageBitmap(5, 5),
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
            Text(currentSong?.name ?: "", style = MaterialTheme.typography.displaySmall)
            Text(currentSong?.artist ?: "", style = MaterialTheme.typography.bodyMedium)
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
            valueRange = 0f..(currentSong?.duration?.toFloat() ?: 0f),
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
            Text(currentSong?.duration?.timeStamp() ?: "..", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

data class PlayState(val playlist: Playlist, val song: Song, val startTime: Int)
infix fun Pair<Playlist, Song>.to(startTime: Int) = PlayState(first, second, startTime)