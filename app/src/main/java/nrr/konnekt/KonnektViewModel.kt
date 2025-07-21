package nrr.konnekt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

internal class KonnektViewModel : ViewModel() {
    var showSplashOnce by mutableStateOf(false)
}