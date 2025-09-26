package nrr.konnekt.core.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

object MediaPlayerManager {
    private var player: ExoPlayer? = null
    private var currentTempFile: File? = null

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs = _currentPositionMs.asStateFlow()

    private val _currentKey = MutableStateFlow<String?>(null)
    val currentKey = _currentKey.asStateFlow()

    fun resumeOrPlayMedia(
        context: Context,
        mediaBytes: ByteArray,
        playerView: PlayerView? = null,
        key: String? = null
    ) {
        if (player == null) create(context)
        when (_playbackState.value) {
            PlaybackState.PAUSED -> {
                player?.play()
            }
            else -> {
                cleanupTempFile()
                _currentKey.value = key
                player?.let { player ->
                    val tempFile = File.createTempFile("media_", ".tmp", context.cacheDir)
                    tempFile.outputStream().use { it.write(mediaBytes) }
                    currentTempFile = tempFile
                    val mediaItem = MediaItem.fromUri(tempFile.toURI().toString())

                    playerView?.player = player
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                    _playbackState.value = PlaybackState.PLAYING
                }
            }
        }
    }

    fun pause() {
        if (player?.playbackState == Player.STATE_READY) {
            player?.pause()
            _playbackState.value = PlaybackState.PAUSED
        }
    }

    fun seekTo(ms: Long) {
        if (player?.playbackState == Player.STATE_READY) {
            player?.seekTo(ms)
        }
    }

    internal fun release() {
        player?.release()
        cleanupTempFile()
    }

    private fun create(context: Context) {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
            player?.addListener(PlayerListener())
        }
    }

    private fun cleanupTempFile() {
        currentTempFile?.let {
            if (it.exists()) it.delete()
            currentTempFile = null
            restartStates()
        }
    }

    private fun restartStates() {
        _playbackState.value = PlaybackState.IDLE
        _currentPositionMs.value = 0L
        _currentKey.value = null
    }

    private class PlayerListener : Player.Listener {
        private val handler = Handler(Looper.getMainLooper())
        private val updatePositionRunnable = object : Runnable {
            override fun run() {
                val currentPositionMs = player?.currentPosition ?: 0L
                _currentPositionMs.value = currentPositionMs
                handler.postDelayed(this, 1000L)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                cleanupTempFile()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            cleanupTempFile()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) handler.post(updatePositionRunnable)
            else handler.removeCallbacks(updatePositionRunnable)
        }
    }
}