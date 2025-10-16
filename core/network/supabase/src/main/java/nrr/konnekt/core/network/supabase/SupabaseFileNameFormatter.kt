package nrr.konnekt.core.network.supabase

import nrr.konnekt.core.domain.FileNameFormatter
import nrr.konnekt.core.model.util.now
import javax.inject.Inject

internal class SupabaseFileNameFormatter @Inject constructor()
    : FileNameFormatter {
    override fun format(rawName: String): String =
        "${now()}_${
            if (rawName.length <= 150) rawName
            else {
                val ext = rawName.substringAfterLast('.')
                val name = rawName.substringBeforeLast('.').take(140)

                "$name${if (ext.isNotEmpty()) ".$ext" else ""}"
            }
        }"

    override fun restore(formattedName: String): String =
        formattedName.substringAfter('_')
}