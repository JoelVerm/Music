import android.content.ContentUris
import android.provider.MediaStore
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import java.io.FileNotFoundException

@Composable
actual fun getDownloadedSongs(): List<Playlist> {
    val contentResolver = LocalContext.current.contentResolver
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1 OR ${MediaStore.Audio.Media.IS_DOWNLOAD} = 1"
    val cursor = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null
    ) ?: return listOf()
    val playlists: MutableList<Playlist> = mutableListOf()
    while (cursor.moveToNext()) {
        var playlistName = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH))
        if (playlistName?.startsWith("Music") == false)
            continue
        playlistName = playlistName?.substringAfter("Music/")?.trim('/')
        if (playlistName.isNullOrEmpty())
            playlistName = NO_PLAYLIST_NAME
        if (!playlists.any { it.name == playlistName })
            playlists.add(Playlist(playlistName))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
        val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
        val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) / 1000
        val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
        val albumId = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
        var albumCover: ImageBitmap
        if (albumId != null) {
            try {
                val albumUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId
                )
                albumCover =
                    contentResolver.loadThumbnail(albumUri, Size(350, 350), null).asImageBitmap()
            } catch (e: FileNotFoundException) {
                albumCover = ImageBitmap(5, 5)
            }
        } else albumCover = ImageBitmap(5, 5)
        playlists.first { it.name == playlistName }.songs.add(Song(title, artist, duration, path, albumCover))
    }
    cursor.close()
    return playlists
}
