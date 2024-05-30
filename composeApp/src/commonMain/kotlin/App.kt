import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
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