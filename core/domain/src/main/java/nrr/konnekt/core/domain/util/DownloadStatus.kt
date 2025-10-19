package nrr.konnekt.core.domain.util

sealed interface DownloadStatus {
    data class Progress(val progress: Float) : DownloadStatus
    data class Error(val message: String) : DownloadStatus
    @JvmInline
    value class Complete(val bytes: ByteArray) : DownloadStatus
}
