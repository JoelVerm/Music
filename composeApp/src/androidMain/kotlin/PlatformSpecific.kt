import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun windowWidth(): Int {
    return LocalConfiguration.current.screenWidthDp
}
