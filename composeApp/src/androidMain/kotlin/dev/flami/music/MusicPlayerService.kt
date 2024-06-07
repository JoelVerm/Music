package dev.flami.music

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import downloadedSongs
import getLastSongProgress

@UnstableApi
class MusicPlayerService: MediaLibraryService() {
    private lateinit var player: Player
    private lateinit var session: MediaLibrarySession

    override fun  onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(applicationContext)
            .setRenderersFactory(
                DefaultRenderersFactory(this).setExtensionRendererMode(
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                )
            ).build()

        session = MediaLibrarySession.Builder(this, player,
            object: MediaLibrarySession.Callback {
                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<MutableList<MediaItem>> {
                    val updatedMediaItems = mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
                    return Futures.immediateFuture(updatedMediaItems)
                }

                override fun onPlaybackResumption(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): ListenableFuture<MediaItemsWithStartPosition> {
                    val settable = SettableFuture.create<MediaItemsWithStartPosition>()
                    val progress = getLastSongProgress(this@MusicPlayerService)
                    val playlist = downloadedSongs(this@MusicPlayerService)
                        .firstOrNull { it.name == progress?.playlist }
                        ?.songs
                    val resumptionPlaylist = playlist
                        ?.map { MediaItem.Builder().setUri(it.path).setMediaId(it.path).build() }
                        ?.let { media -> MediaItemsWithStartPosition(
                            media,
                            playlist.indexOfFirst { it.name == progress?.song },
                            progress?.progress?.toLong() ?: 0
                        ) }
                    settable.set(resumptionPlaylist)
                    return settable
                }
            }).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = session

    override fun onDestroy() {
        super.onDestroy()
        session.release()
        player.release()
    }
}