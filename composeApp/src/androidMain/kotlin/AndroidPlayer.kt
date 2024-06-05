import android.app.Notification
import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken

@Composable
actual fun player(): State<Player> {
    val ctx = LocalContext.current
    return remember { mutableStateOf(AndroidPlayer(ctx)) }
}

@OptIn(UnstableApi::class)
class AndroidPlayer (context: Context) : Player {
    private val player = ExoPlayer.Builder(context).build()

    private var playlist: List<Song> = listOf()

    override fun load(playlist: List<Song>) {
        this.playlist = playlist
        player.setMediaItems(playlist.map { MediaItem.fromUri(it.path) })
        player.prepare()
        player.seekToDefaultPosition(0)
    }

    override fun goto(song: Song) {
        player.seekToDefaultPosition(playlist.indexOf(song))
    }

    override fun currentSong(): Song? = playlist.getOrNull(player.currentMediaItemIndex)

    override fun playing(state: Boolean) {
        if (state == player.isPlaying) return
        if (state) player.play()
        else player.pause()
    }

    override fun playing(): Boolean = player.isPlaying

    override fun seekTo(position: Int) {
        player.seekTo(position * 1000L)
    }

    override fun progress(): Int {
        return (player.currentPosition.toInt()) / 1000
    }

    override fun next() {
        player.seekToNextMediaItem()
    }

    override fun previous() {
        player.seekToPreviousMediaItem()
    }

    override fun shuffle(state: Boolean) {
        player.shuffleModeEnabled = state
    }

    override fun repeat(state: Boolean) {
        player.repeatMode = if (state) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
    }
}
