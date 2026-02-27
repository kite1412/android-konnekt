package nrr.konnekt.core.media

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

object AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    @RequiresApi(android.os.Build.VERSION_CODES.S)
    fun startRecording(context: Context): Boolean {
        stopRecording()
        outputFile = createTempAudioFile(context)
        outputFile?.let { file ->
            recorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            return true
        }
        return false
    }

    fun stopRecording(): File? {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        return outputFile
    }

    fun deleteTempAudioFile() {
        if (outputFile?.delete() == false) {
            Log.e("AudioRecorder", "Failed to delete temporary audio file")
        }
        outputFile = null
    }

    private fun createTempAudioFile(context: Context): File =
        File.createTempFile(
            /*prefix=*/"audio_",
            /*suffix=*/".m4a",
            /*directory=*/context.cacheDir
        )
}