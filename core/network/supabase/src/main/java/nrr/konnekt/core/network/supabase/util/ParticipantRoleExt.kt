package nrr.konnekt.core.network.supabase.util

import nrr.konnekt.core.model.ParticipantRole

internal fun ParticipantRole.toSupabaseEnum() = toString().lowercase()