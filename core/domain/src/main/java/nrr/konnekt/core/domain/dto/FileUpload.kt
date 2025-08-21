package nrr.konnekt.core.domain.dto

data class FileUpload(
    val fileName: String,
    val fileExtension: String,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileUpload

        if (fileName != other.fileName) return false
        if (fileExtension != other.fileExtension) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + fileExtension.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
