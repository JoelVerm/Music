import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration

actual fun byteArrayToImage(image: ByteArray): ImageBitmap {
    return BitmapFactory.decodeByteArray(image, 0, image.size).asImageBitmap()
}

@Composable
actual fun windowWidth(): Int {
    return LocalConfiguration.current.screenWidthDp
}