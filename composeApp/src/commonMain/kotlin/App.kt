import androidx.compose.runtime.*
import dev.flami.music.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
        darkTheme: Boolean = true,
        dynamicColor: Boolean = false
    ) {
    AppTheme(
        darkTheme,
        dynamicColor
    ) {
        NavLayout(
            listOf(
                HomeScreen,
                SearchScreen(5),
                ProfileScreen("Hey"),
            )
        )
    }
}