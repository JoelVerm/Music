import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalWindowInfo

actual fun log(tag: String, message: String) {
    println("[$tag] $message")
}

@Composable
actual fun requestPermissions(): State<Boolean> {
    return remember { mutableStateOf(true) }
}

@Composable
actual fun getDownloadedSongs(): List<Playlist> {
    // TODO
    return listOf(
        Playlist("playlist", mutableListOf(
            Song("title", "artist", 123, "url", ImageBitmap(5, 5))
        ))
    )
}

@Composable
actual fun getLastSongProgress(): SongProgress? {
    // TODO
    return SongProgress("playlist", "song", 10)
}

@Composable
actual fun player(): State<Player> {
    return remember { mutableStateOf(DesktopPlayer()) }
}

class DesktopPlayer: Player {
    override fun load(playlist: Playlist) {
        // TODO
    }

    override fun goto(song: Song) {
        // TODO
    }

    override fun goto(song: Song, progress: Int) {
        // TODO
    }

    override fun currentSong(): Song? {
        // TODO
        return Song("title", "artist", 123, "url", ImageBitmap(5, 5))
    }

    override fun playing(state: Boolean) {
        // TODO
    }

    override fun playing(): Boolean {
        // TODO
        return true
    }

    override fun seekTo(position: Int) {
        // TODO
    }

    override fun progress(): Int {
        // TODO
        return 10
    }

    override fun next() {
        // TODO
    }

    override fun previous() {
        // TODO
    }

    override fun shuffle(state: Boolean) {
        // TODO
    }

    override fun repeat(state: Boolean) {
        // TODO
    }

}