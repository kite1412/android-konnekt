package nrr.konnekt.core.domain.dto

data class FileUpload(
    val fileName: String,
    val fileType: String,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileUpload

        if (fileName != other.fileName) return false
        if (fileType != other.fileType) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + fileType.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
