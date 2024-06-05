import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun getDownloadedSongs(): State<List<Playlist>?>

@Composable
expect fun player(): State<Player>

data class Playlist(val name: String, val songs: MutableList<Song> = mutableListOf())
class Song(
    name: String,
    artist: String,
    val duration: Int,
    val path: String,
    val cover: ImageBitmap,
    val content: ByteArray = byteArrayOf()
) {
    val name: String = name.cutoff(35)
    val artist: String = artist.cutoff(35)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Song
        return name == other.name && artist == other.artist
    }

    override fun hashCode() = name.hashCode()
}

interface Player {
    fun load(playlist: List<Song>)
    fun goto(song: Song)
    fun currentSong(): Song?
    fun playing(state: Boolean)
    fun playing(): Boolean
    fun seekTo(position: Int)
    fun progress(): Int
    fun next()
    fun previous()
    fun shuffle(state: Boolean)
    fun repeat(state: Boolean)
}