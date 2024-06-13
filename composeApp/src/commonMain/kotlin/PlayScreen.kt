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
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Update
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

val PlayScreen = NavScreen("Play", Icons.Rounded.PlayArrow,
    PlayState(listOf(), "", "", 0)) {
    val player by player()

    val playlists = it.prop.playlists
    val playlist by rememberDerived(it.prop.playlist) { pl ->
        (playlists.find { it.name == pl } ?: playlists.firstOrNull())?.also { player.load(it) }
    }
    val startSong by rememberDerived(it.prop) { sn ->
        (playlist?.songs?.find { it.name == sn.song } ?: playlist?.songs?.firstOrNull())?.also { player.goto(it, sn.startTime) }
    }

    var currentSong by remember(startSong) { mutableStateOf(startSong) }
    var songProgress by remember(startSong) { mutableStateOf(it.prop.startTime.toFloat()) }

    var playing by rememberWith(player.playing()) {
        player.playing(it)
    }
    var shuffle by rememberWith(playlist, false) {
        player.shuffle(it)
    }
    var repeat by rememberWith(playlist, false) {
        player.repeat(it)
    }

    LaunchedEffect(Unit) {
        while(true) {
            delay(500)
            player.currentSong()?.let { currentSong = it }
            songProgress = player.progress().toFloat()
        }
    }

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

    Column(
        modifier = Modifier.fillMaxWidth().weight(1f).padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(bitmap = currentSong?.cover ?: ImageBitmap(5, 5),
            contentDescription = "Album art",
            modifier = Modifier.width(350.dp).aspectRatio(1f).clip(RoundedCornerShape(25.dp)).weight(1f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Column(
            modifier = Modifier.padding(0.dp, 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(playlist?.name ?: "", style = MaterialTheme.typography.titleMedium)
            Text(currentSong?.name ?: "", style = MaterialTheme.typography.displaySmall, textAlign = TextAlign.Center)
            Text(currentSong?.artist ?: "", style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                content = { Icon(Icons.Rounded.Shuffle, "Shuffle") },
                onClick = { shuffle = !shuffle },
                colors = if (shuffle) secondaryColor else transparentColor,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Button(
                content = { Icon(Icons.Rounded.SkipPrevious, "Previous") },
                onClick = { player.previous() },
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
                onClick = { player.next() },
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
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                songProgress.toInt().timeStamp(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
            Button(
                content = { Icon(Icons.Rounded.History, "Rewind 10 seconds") },
                onClick = {
                    player.seekTo(songProgress.toInt() - 10)
                    songProgress -= 10
                },
                colors = transparentColor,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.weight(1f)
            )
            Button(
                content = { Icon(Icons.Rounded.Update, "Skip 10 seconds") },
                onClick = {
                    player.seekTo(songProgress.toInt() + 10)
                    songProgress += 10
                },
                colors = transparentColor,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.weight(1f)
            )
            Text(
                currentSong?.duration?.timeStamp() ?: "..",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

data class PlayState(val playlists: List<Playlist>, val playlist: String, val song: String, val startTime: Int) {
    constructor(playlists: List<Playlist>) : this(playlists, playlists.first().name, playlists.first().songs.first().name, 0)
}
infix fun PlayState.from(playlist: String) = PlayState(playlists, playlist, "", 0)
infix fun PlayState.play(song: String) = PlayState(playlists, playlist, song, 0)
infix fun PlayState.at(progress: Int) = PlayState(playlists, playlist, song, progress)
fun NavScreen<PlayState>.load(other: (PlayState) -> PlayState) = invoke(other(prop))