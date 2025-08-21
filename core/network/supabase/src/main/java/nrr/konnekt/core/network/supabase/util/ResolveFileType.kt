package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.network.supabase.util.FileType.allowedDocumentExtensions
import nrr.konnekt.core.network.supabase.util.FileType.allowedImageExtensions
import nrr.konnekt.core.network.supabase.util.FileType.allowedVideoExtensions

internal fun resolveFileType(fileExtension: String) =
    (if (allowedImageExtensions.contains(fileExtension))
        FileType.Enum.IMAGE
    else if (allowedVideoExtensions.contains(fileExtension))
        FileType.Enum.VIDEO
    else if (allowedVideoExtensions.contains(fileExtension))
        FileType.Enum.AUDIO
    else if (fileExtension == "txt")
        FileType.Enum.PLAIN_TEXT
    else if (allowedDocumentExtensions.contains(fileExtension))
        FileType.Enum.APPLICATION
    else throw IllegalArgumentException("Invalid file extension: $fileExtension"))
        .toString()