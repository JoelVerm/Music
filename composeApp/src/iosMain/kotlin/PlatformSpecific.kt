import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun windowWidth(): Int {
    return LocalWindowInfo.current.containerSize.width
}

@Composable
actual fun getDownloadedSongs(): State<List<Playlist>?> {
    TODO()
}