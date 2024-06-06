import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import dev.flami.music.MusicPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun player(): State<Player> {
    val ctx = LocalContext.current
    return remember { mutableStateOf(AndroidPlayer(ctx)) }
}

const val PREFERENCES_FILE_KEY = "song_progress"
const val SONG_PROGRESS_KEY = "song_progress"
@Composable
actual fun getLastSongProgress(): SongProgress? = getLastSongProgress(LocalContext.current)
fun getLastSongProgress(context: Context): SongProgress? {
    return context
        .getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        .getString(SONG_PROGRESS_KEY, null)
        ?.let {
            val parts = it.split("|||")
            if (parts.size == 3) SongProgress(parts[0], parts[1], parts[2].toInt())
            else null
        }
        ?.apply { Log.d("songProgress", "Load playlist: $playlist, song: $song, progress: $progress") }
}
fun saveLastSongProgress(context: Context, playlist: String, song: String, progress: Int) {
    if (playlist.isEmpty() || song.isEmpty()) return
    Log.d("songProgress", "Save playlist: $playlist, song: $song, progress: $progress")
    context
        .getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        .edit()
        .putString(SONG_PROGRESS_KEY, "$playlist|||$song|||$progress")
        .apply()
}

@OptIn(UnstableApi::class)
class AndroidPlayer (context: Context) : Player {
    private val sessionToken = SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
    private val future = MediaController.Builder(context, sessionToken).buildAsync()
    private var player: MediaController? = null
    private val preInitCallsToFinish: MutableList<() -> Unit> = mutableListOf()
    private val scope = CoroutineScope(Dispatchers.Default)
    private var playlist: Playlist? = null

    init {
        future.addListener({
            player = future.get()
            preInitCallsToFinish.forEach { it() }
        }, MoreExecutors.directExecutor())

        scope.launch {
            while (true) {
                withContext(Dispatchers.Main) {
                    saveLastSongProgress(
                        context,
                        playlist?.name ?: "",
                        currentSong()?.name ?: "",
                        progress()
                    )
                }
                delay(1000)
            }
        }
    }

    override fun load(playlist: Playlist) {
        checkPlayer { load(playlist) } ?: return
        this.playlist = playlist
        player?.setMediaItems(playlist.songs.map { MediaItem.Builder().setUri(it.path).setMediaId(it.path).build() })
        player?.prepare()
        player?.seekToDefaultPosition(0)
    }

    override fun goto(song: Song) {
        checkPlayer { goto(song) } ?: return
        player?.seekToDefaultPosition(playlist?.songs?.indexOf(song) ?: 0)
    }

    override fun currentSong(): Song? =
        checkPlayer()?.run { player?.let { playlist?.songs?.getOrNull(it.currentMediaItemIndex) } }

    override fun playing(state: Boolean) {
        checkPlayer { playing(state) } ?: return
        if (state == player?.isPlaying) return
        if (state) player?.play()
        else player?.pause()
    }

    override fun playing(): Boolean = player?.isPlaying ?: false

    override fun seekTo(position: Int) {
        checkPlayer { seekTo(position) } ?: return
        player?.seekTo(position * 1000L)
    }

    override fun progress(): Int {
        checkPlayer() ?: return 0
        return (player?.currentPosition?.toInt() ?: 0) / 1000
    }

    override fun next() {
        checkPlayer { next() } ?: return
        player?.seekToNextMediaItem()
    }

    override fun previous() {
        checkPlayer { previous() } ?: return
        player?.seekToPreviousMediaItem()
    }

    override fun shuffle(state: Boolean) {
        checkPlayer { shuffle(state) } ?: return
        player?.shuffleModeEnabled = state
    }

    override fun repeat(state: Boolean) {
        checkPlayer { repeat(state) } ?: return
        player?.repeatMode = if (state) ExoPlayer.REPEAT_MODE_ALL else ExoPlayer.REPEAT_MODE_OFF
    }

    private fun checkPlayer() = checkPlayer(null)
    private fun checkPlayer(then: (() -> Unit)?): Unit? {
        if (player == null) {
            if (then != null)
                preInitCallsToFinish.add(then)
            return null
        }
        return Unit
    }
}
