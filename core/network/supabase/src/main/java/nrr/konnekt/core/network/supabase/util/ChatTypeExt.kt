package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.ChatType

internal fun ChatType.toSupabaseEnum() = toString().lowercase()