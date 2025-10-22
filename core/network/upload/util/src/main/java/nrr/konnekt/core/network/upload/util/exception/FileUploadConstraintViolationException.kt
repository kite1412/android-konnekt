package nrr.konnekt.core.network.upload.util.exception

import nrr.konnekt.core.network.upload.util.ViolationReason

data class FileUploadConstraintViolationException(
    override val message: String?,
    val reason: ViolationReason
) : RuntimeException()