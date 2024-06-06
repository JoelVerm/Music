import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo

actual fun log(tag: String, message: String) {
    println("[$tag] $message")
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun windowWidth(): Int {
    return LocalWindowInfo.current.containerSize.width
}

@Composable
actual fun requestPermissions(): State<Boolean> {
    TODO()
}

@Composable
actual fun getDownloadedSongs(): List<Playlist> {
    TODO()
}

@Composable
actual fun getLastSongProgress(): SongProgress? {
    TODO()
}

@Composable
actual fun player(): State<Player> {
    TODO()
}