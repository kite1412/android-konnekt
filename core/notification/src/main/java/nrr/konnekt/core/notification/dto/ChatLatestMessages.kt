package nrr.konnekt.core.notification.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
internal data class ChatLatestMessages(
    val chat: Chat,
    @SerialName("latest_messages")
    val latestMessages: List<LatestMessage>
)

internal fun ChatLatestMessages(map: Map<String, String>): ChatLatestMessages =
    with(Json { isLenient = true }) {
        decodeFromJsonElement(
            buildJsonObject {
                map.forEach { (key, value) ->
                    val element = try {
                        parseToJsonElement(value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        JsonPrimitive(value)
                    }
                    put(key, element)
                }
            }
        )
    }