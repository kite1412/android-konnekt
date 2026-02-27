package nrr.konnekt.core.network.supabase

import nrr.konnekt.core.model.util.now
import nrr.konnekt.core.network.upload.domain.FileNameFormatter
import javax.inject.Inject

internal class SupabaseFileNameFormatter @Inject constructor()
    : FileNameFormatter {
    override fun format(rawName: String, ext: String): String =
        "${now()}_${
            if (rawName.length <= 150) rawName
            else rawName.substringBeforeLast('.').take(140)
        }${
            if (rawName.substringAfterLast('.') != ext) {
                ".$ext"
            } else ""
        }"

    override fun restore(formattedName: String): String =
        formattedName.substringAfter('_')
}