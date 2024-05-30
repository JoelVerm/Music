import androidx.compose.foundation.Image
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
import androidx.compose.material3.Text
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
    val (playlistID, songID) = it.prop
    val playlistName = getPlaylistName(playlistID)
    val songURL = getSongURL(songID)
    val songName = getSongName(songID)
    val artistName = getArtistName(songID)

    var image by remember { mutableStateOf(ImageBitmap(500, 500)) }
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch { image = loadPicture(songURL) }

    var playing by remember { mutableStateOf(false) }

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
            Button(
                content = { Icon(Icons.Rounded.Shuffle, "Shuffle") },
                onClick = { /*TODO*/ },
                colors = transparentColor,
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
                onClick = { /*TODO*/ },
                colors = transparentColor,
                contentPadding = PaddingValues(0.dp)
            )
        }
    }
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