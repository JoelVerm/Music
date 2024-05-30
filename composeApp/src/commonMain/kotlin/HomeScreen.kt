import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.launch

val HomeScreen = NavScreen("Home", Icons.Rounded.Home,
    "https://random.dog/8c9dd457-5907-4fd4-a825-f8c576fe1284.jpeg") {
    val songURL = it.prop
    var image by remember { mutableStateOf(ImageBitmap(500, 500)) }
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch { image = loadPicture(songURL) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(25.dp).clip(RoundedCornerShape(25.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(bitmap = image,
            contentDescription = "Album art",
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
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