import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.util.Log
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
import com.google.common.util.concurrent.MoreExecutors
import dev.flami.music.MusicPlayerService

@Composable
actual fun player(): State<Player> {
    val ctx = LocalContext.current
    return remember { mutableStateOf(AndroidPlayer(ctx)) }
}

@OptIn(UnstableApi::class)
class AndroidPlayer (context: Context) : Player {
    private val sessionToken = SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
    private val future = MediaController.Builder(context, sessionToken).buildAsync()
    private var player: MediaController? = null
    private val preInitCallsToFinish: MutableList<() -> Unit> = mutableListOf()
    init {
        future.addListener({
            player = future.get()
            preInitCallsToFinish.forEach { it() }
            Log.d("AndroidPlayer", "Player ready!")
        }, MoreExecutors.directExecutor())
    }

    private var playlist: List<Song> = listOf()

    override fun load(playlist: List<Song>) {
        checkPlayer {
            preInitCallsToFinish.add { load(playlist) }
        } ?: return
        this.playlist = playlist
        player?.setMediaItems(playlist.map { MediaItem.Builder().setUri(it.path).setMediaId(it.path).build() })
        player?.prepare()
        player?.seekToDefaultPosition(0)
    }

    override fun goto(song: Song) {
        checkPlayer {
            preInitCallsToFinish.add { goto(song) }
        } ?: return
        player?.seekToDefaultPosition(playlist.indexOf(song))
    }

    override fun currentSong(): Song? =
        checkPlayer {} ?.run { player?.let { playlist.getOrNull(it.currentMediaItemIndex) } }

    override fun playing(state: Boolean) {
        checkPlayer {
            preInitCallsToFinish.add { playing(state) }
        } ?: return
        if (state == player?.isPlaying) return
        if (state) player?.play()
        else player?.pause()
    }

    override fun playing(): Boolean = player?.isPlaying ?: false

    override fun seekTo(position: Int) {
        checkPlayer {
            preInitCallsToFinish.add { seekTo(position) }
        } ?: return
        player?.seekTo(position * 1000L)
    }

    override fun progress(): Int {
        checkPlayer {} ?: return 0
        return (player?.currentPosition?.toInt() ?: 0) / 1000
    }

    override fun next() {
        checkPlayer {
            preInitCallsToFinish.add { next() }
        } ?: return
        player?.seekToNextMediaItem()
    }

    override fun previous() {
        checkPlayer {
            preInitCallsToFinish.add { previous() }
        } ?: return
        player?.seekToPreviousMediaItem()
    }

    override fun shuffle(state: Boolean) {
        checkPlayer {
            preInitCallsToFinish.add { shuffle(state) }
        } ?: return
        player?.shuffleModeEnabled = state
    }

    override fun repeat(state: Boolean) {
        checkPlayer {
            preInitCallsToFinish.add { repeat(state) }
        } ?: return
        player?.repeatMode = if (state) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
    }

    private fun checkPlayer(then: () -> Unit): Unit? {
        if (player == null) {
            then()
            Log.d("AndroidPlayer", "Player not ready")
            return null
        }
        return Unit
    }
}
