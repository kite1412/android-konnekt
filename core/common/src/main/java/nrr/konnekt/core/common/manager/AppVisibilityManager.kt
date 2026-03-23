package nrr.konnekt.core.common.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVisibilityManager @Inject constructor() {
    private val _isForeground = MutableStateFlow(false)
    val isForeground = _isForeground.asStateFlow()

    fun setForeground(isForeground: Boolean) {
        _isForeground.value = isForeground
    }
}