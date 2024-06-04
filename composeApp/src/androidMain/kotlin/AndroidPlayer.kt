import android.media.AudioAttributes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun player(): State<Player> = remember { mutableStateOf(AndroidPlayer()) }

class AndroidPlayer : Player {
    private val player = android.media.MediaPlayer()

    override fun load(song: Song) {
        val wasPlaying = player.isPlaying
        player.reset()
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        player.setDataSource(song.path)
        player.prepare()
        if (wasPlaying) player.start()
    }

    override fun playing(state: Boolean) {
        if (state == player.isPlaying) return
        if (state) player.start()
        else player.pause()
    }

    override fun playing(): Boolean = player.isPlaying

    override fun seekTo(position: Int) {
        player.seekTo(position * 1000)
    }

    override fun progress(): Int {
        return player.currentPosition / 1000
    }

    override fun onComplete(callback: () -> Unit) {
        player.setOnCompletionListener { callback() }
    }
}