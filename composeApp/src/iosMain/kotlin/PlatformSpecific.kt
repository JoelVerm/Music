import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap

actual fun byteArrayToImage(image: ByteArray): ImageBitmap {
    return Image.makeFromEncoded(image).toComposeImageBitmap()
}