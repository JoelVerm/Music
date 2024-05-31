import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.launch

val HomeScreen = NavScreen("Home", Icons.Rounded.Home,
    Pair("playlist1", "song1")) {
    val playlistID by remember { mutableStateOf(it.prop.first) }
    val songID by remember { mutableStateOf(it.prop.second) }
    val playlistName by remember { derivedStateOf { getPlaylistName(playlistID) } }
    val songURL by remember { derivedStateOf { getSongURL(songID) } }
    val songName by remember { derivedStateOf { getSongName(songID) } }
    val songLength by remember { derivedStateOf { getSongLength(songID) } }
    val artistName by remember { derivedStateOf { getArtistName(songID) } }

    var image by remember { mutableStateOf(ImageBitmap(500, 500)) }
    val coroutineScope = rememberCoroutineScope()
    remember { coroutineScope.launch { image = loadPicture(songURL) } }

    var playing by remember { mutableStateOf(false) }
    var songProgress by remember { mutableStateOf(0f) }
    var shuffle by remember { mutableStateOf(false) }
    var repeat by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(bitmap = image,
            contentDescription = "Album art",
            modifier = Modifier.width(350.dp).aspectRatio(1f).clip(RoundedCornerShape(25.dp)),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Column(
            modifier = Modifier.padding(0.dp, 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(playlistName, style = MaterialTheme.typography.titleMedium)
            Text(songName, style = MaterialTheme.typography.displaySmall)
            Text(artistName, style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
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
            )
            Button(
                content = { Icon(Icons.Rounded.SkipPrevious, "Previous") },
                onClick = { /*TODO*/ },
                colors = transparentColor,
                contentPadding = PaddingValues(0.dp)
            )
            Button(
                content = { if (playing)
                    Icon(Icons.Rounded.Pause, "Pause")
                    else Icon(Icons.Rounded.PlayArrow, "Play")
                },
                onClick = { playing = !playing },
                contentPadding = PaddingValues(0.dp)
            )
            Button(
                content = { Icon(Icons.Rounded.SkipNext, "Next") },
                onClick = { /*TODO*/ },
                colors = transparentColor,
                contentPadding = PaddingValues(0.dp)
            )
            Button(
                content = { Icon(Icons.Rounded.Repeat, "Repeat") },
                onClick = { repeat = !repeat },
                colors = if (repeat) secondaryColor else transparentColor,
                contentPadding = PaddingValues(0.dp)
            )
        }
        Slider(
            value = songProgress,
            onValueChange = { value -> songProgress = value },
            modifier = Modifier.fillMaxWidth().padding(0.dp, 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text((songLength * songProgress).toInt().timeStamp(), style = MaterialTheme.typography.bodyMedium)
            Text(songLength.timeStamp(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun Int.timeStamp(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
}

suspend fun loadPicture(url: String): ImageBitmap {
    val client = HttpClient()
    try {
        val response = client.get(url)
        val image = response.readBytes()
        return byteArrayToImage(image)
    }
    catch (e: Exception) {
        return ImageBitmap(500, 500)
    }
    finally {
        client.close()
    }
}

expect fun byteArrayToImage(image: ByteArray): ImageBitmap