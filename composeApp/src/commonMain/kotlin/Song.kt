import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun getDownloadedSongs(): State<List<Playlist>?>

data class Playlist(val name: String, val songs: MutableList<Song> = mutableListOf())
class Song(
    name: String,
    artist: String,
    val duration: Int,
    val path: String,
    val cover: ImageBitmap,
    val content: ByteArray = byteArrayOf()
) {
    val name: String
    val artist: String
    init {
        this.name = name.cutoff(35)
        this.artist = artist.cutoff(35)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Song
        return name == other.name && artist == other.artist
    }

    override fun hashCode() = name.hashCode()
}
