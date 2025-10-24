package nrr.konnekt.core.media

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nrr.konnekt.core.media.util.LOG_TAG
import java.io.File

object MediaPlayerManager {
    var player: ExoPlayer? = null
        private set
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
        key: String,
        playerView: PlayerView? = null
    ) {
        if (player == null) create(context)
        if (_playbackState.value == PlaybackState.PAUSED && _currentKey.value == key)
            player?.play()
        else {
            cleanupTempFile()
            restartStates()
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

    fun pause() {
        if (player?.playbackState == Player.STATE_READY) {
            player?.pause()
            _playbackState.value = PlaybackState.PAUSED
        }
    }

    fun seekTo(ms: Long) {
        if (player?.playbackState == Player.STATE_READY) {
            player?.seekTo(ms)
            _currentPositionMs.value = ms
        }
    }

    fun clearPlayback() {
        player?.stop()
        cleanupTempFile()
        restartStates()
    }

    internal fun release() {
        player?.release()
        player = null
        cleanupTempFile()
        restartStates()
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
        }
    }

    private fun restartStates() {
        _playbackState.value = PlaybackState.IDLE
        _currentPositionMs.value = 0L
        _currentKey.value = null
    }

    private class PlayerListener : Player.Listener {
        private var positionJob: Job? = null

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            _playbackState.value = when (playbackState) {
                Player.STATE_IDLE -> PlaybackState.IDLE
                Player.STATE_READY -> PlaybackState.PLAYING
                Player.STATE_ENDED -> PlaybackState.ENDED
                else -> PlaybackState.IDLE
            }
            Log.d(LOG_TAG, "Playback state: ${_playbackState.value}")
            if (playbackState == Player.STATE_ENDED) {
                cleanupTempFile()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            cleanupTempFile()
            restartStates()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                positionJob = CoroutineScope(Dispatchers.Main).launch {
                    while (true) {
                        val currentPositionMs = player?.currentPosition ?: 0L
                        _currentPositionMs.value = currentPositionMs
                        delay(100L)
                    }
                }
            } else {
                positionJob?.cancel()
                positionJob = null
            }
        }
    }
}