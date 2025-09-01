package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.util.AllowedFileType

internal fun resolveFileType(fileExtension: String) =
    AllowedFileType.isExtensionAllowed(fileExtension)
        ?.toString()
        ?.lowercase()
        ?: throw IllegalArgumentException("Invalid file extension: $fileExtension")