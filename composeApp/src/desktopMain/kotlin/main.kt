import androidx.compose.foundation.isSystemInDarkTheme
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import music.composeapp.generated.resources.Res
import music.composeapp.generated.resources.icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Music",
        icon = painterResource(Res.drawable.icon)
    ) {
        App(
            darkTheme = isSystemInDarkTheme(),
            dynamicColor = false
        )
    }
}