package nrr.konnekt.feature.conversation.util

internal sealed interface MessageComposerAction {
    object Attachment : MessageComposerAction
    object Voice : MessageComposerAction
}