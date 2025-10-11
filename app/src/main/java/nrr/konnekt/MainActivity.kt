package nrr.konnekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import nrr.konnekt.core.domain.UserPresenceManager
import nrr.konnekt.core.ui.compositionlocal.LocalStatusBarColorManager
import nrr.konnekt.ui.KonnektApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPresenceManager: UserPresenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: KonnektViewModel = hiltViewModel()

            CompositionLocalProvider(
                LocalStatusBarColorManager provides vm.statusBarColorUpdater
            ) {
                KonnektApp(viewModel = vm)
                StatusBarProtection(vm.statusBarColor)
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                userPresenceManager.markUserInactive()
            }
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                userPresenceManager.markUserActive()
            }
        }
    }

    @Composable
    private fun StatusBarProtection(
        color: Color,
        heightProvider: () -> Float = statusBarHeight()
    ) {
        val density = LocalDensity.current

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) {
                    heightProvider().toDp()
                })
        ) {
            val height = heightProvider()

            drawRect(
                color = color,
                size = Size(size.width, height)
            )
        }
    }

    @Composable
    private fun statusBarHeight(): () -> Float {
        val statusBars = WindowInsets.statusBars
        val density = LocalDensity.current
        return { statusBars.getTop(density).toFloat() }
    }
}