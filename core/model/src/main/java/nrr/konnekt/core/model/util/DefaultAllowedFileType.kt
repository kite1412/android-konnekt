package nrr.konnekt.core.model.util

object DefaultAllowedFileType {
    val imageTypes = listOf(
        FileType("image/jpeg", "jpg"),
        FileType("image/jpeg", "jpeg"),
        FileType("image/png", "png"),
        FileType("image/heif", "heif"),
        FileType("image/heic", "heic"),
    )
    val videoTypes = listOf(
        FileType("video/mp4", "mp4")
    )
    val audioTypes = listOf(
        FileType("audio/mpeg", "mp3")
    )
    val documentTypes = listOf(
        FileType("application/pdf", "pdf"),
        FileType("application/msword", "doc"),
        FileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
        FileType("application/vnd.ms-excel", "xls"),
        FileType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
        FileType("application/vnd.ms-powerpoint", "ppt"),
        FileType("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
        FileType("application/zip", "zip"),
        FileType("plain/text", "txt")
    )
}