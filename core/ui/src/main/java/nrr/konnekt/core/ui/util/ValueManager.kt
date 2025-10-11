package nrr.konnekt.core.ui.util

interface ValueManager<T> {
    fun update(newValue: T): T

    fun reset(): T
}