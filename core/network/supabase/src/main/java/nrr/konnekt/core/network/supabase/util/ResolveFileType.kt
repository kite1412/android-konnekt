package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.util.AllowedAttachmentExtension.audioExtensions
import nrr.konnekt.core.model.util.AllowedAttachmentExtension.documentExtensions
import nrr.konnekt.core.model.util.AllowedAttachmentExtension.imageExtensions
import nrr.konnekt.core.model.util.AllowedAttachmentExtension.videoExtensions

internal fun resolveFileType(fileExtension: String) =
    (if (imageExtensions.contains(fileExtension))
        FileType.IMAGE
    else if (videoExtensions.contains(fileExtension))
        FileType.VIDEO
    else if (audioExtensions.contains(fileExtension))
        FileType.AUDIO
    else if (fileExtension == "txt")
        FileType.PLAIN_TEXT
    else if (documentExtensions.contains(fileExtension))
        FileType.APPLICATION
    else throw IllegalArgumentException("Invalid file extension: $fileExtension"))
        .toString()