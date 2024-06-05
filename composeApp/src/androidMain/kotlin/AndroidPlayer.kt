import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.flami.music.PlaybackService

@Composable
actual fun player(): State<Player> {
    val ctx = LocalContext.current
    return remember { mutableStateOf(AndroidPlayer(ctx)) }
}

class AndroidPlayer(context: Context) : Player {
    private val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
    private val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    private var player: MediaController? = null

    init {
        controllerFuture.addListener(
            {
                player = controllerFuture.get().also { it.prepare() }
            },
            MoreExecutors.directExecutor()
        )
    }

    private var playlist: List<Song> = listOf()

    override fun load(playlist: List<Song>) {
        this.playlist = playlist
        player?.setMediaItems(playlist.map { MediaItem.fromUri(it.path) })
    }

    override fun goto(song: Song) {
        player?.seekToDefaultPosition(playlist.indexOf(song))
    }

    override fun currentSong(): Song? = player?.let { playlist.getOrNull(it.currentMediaItemIndex) }

    override fun playing(state: Boolean) {
        if (state == player?.isPlaying) return
        if (state) player?.play()
        else player?.pause()
    }

    override fun playing(): Boolean = player?.isPlaying ?: false

    override fun seekTo(position: Int) {
        player?.seekTo(position * 1000L)
    }

    override fun progress(): Int {
        return (player?.currentPosition?.toInt() ?: 0) / 1000
    }

    override fun next() {
        player?.seekToNextMediaItem()
    }

    override fun previous() {
        player?.seekToPreviousMediaItem()
    }

    override fun shuffle(state: Boolean) {
        player?.shuffleModeEnabled = state
    }

    override fun repeat(state: Boolean) {
        player?.repeatMode = if (state) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
    }
}