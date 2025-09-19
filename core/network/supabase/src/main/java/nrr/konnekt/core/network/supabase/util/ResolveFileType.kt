package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.util.DefaultAllowedFileType

internal fun resolveFileType(fileExtension: String) =
    DefaultAllowedFileType.isExtensionAllowed(fileExtension)
        ?.toString()
        ?.lowercase()
        ?: throw IllegalArgumentException("Invalid file extension: $fileExtension")