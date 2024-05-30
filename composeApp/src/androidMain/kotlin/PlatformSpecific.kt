import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun byteArrayToImage(image: ByteArray): ImageBitmap {
    return BitmapFactory.decodeByteArray(image, 0, image.size).asImageBitmap()
}