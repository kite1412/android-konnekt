package nrr.konnekt.core.domain

/**
 * Contract for formatting and restoring of file names.
 */
interface FileNameFormatter {
    /**
     * Formats a raw name file.
     *
     * This method doesn't need to be implemented if formatting is not required
     * or is handled on the backend.
     *
     * @see restore for restoring a formatted file name.
     * @param rawName the actual file name before formatting.
     * @return the formatted file name.
     */
    fun format(rawName: String): String

    /**
     * Restores a formatted file name to the actual name of the file.
     *
     * @see format for formatting a file name.
     * @param formattedName the file name that was previously formatted using [format]
     * @return the actual file name.
     */
    fun restore(formattedName: String): String
}