package nrr.konnekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
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
import nrr.konnekt.core.ui.compositionlocal.LocalNavigationBarColorManager
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

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CompositionLocalProvider(
                    LocalStatusBarColorManager provides vm.statusBarColorManager,
                    LocalNavigationBarColorManager provides vm.navigationBarColorManager
                ) {
                    KonnektApp(viewModel = vm)
                    StatusBarProtection(vm.statusBarColor)
                    NavigationBarProtection(
                        color = vm.navigationBarColor,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
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
        modifier: Modifier = Modifier,
        heightProvider: () -> Float = statusBarHeight()
    ) = DrawRect(
        color = color,
        heightPxProvider = heightProvider,
        modifier = modifier
    )

    @Composable
    private fun statusBarHeight(): () -> Float {
        val statusBars = WindowInsets.statusBars
        val density = LocalDensity.current
        return { statusBars.getTop(density).toFloat() }
    }

    @Composable
    private fun NavigationBarProtection(
        color: Color,
        modifier: Modifier = Modifier,
        heightProvider: () -> Float = navigationBarHeight()
    ) = DrawRect(
        color = color,
        heightPxProvider = heightProvider,
        modifier = modifier
    )

    @Composable
    private fun navigationBarHeight(): () -> Float {
        val statusBars = WindowInsets.navigationBars
        val density = LocalDensity.current
        return { statusBars.getBottom(density).toFloat() }
    }

    @Composable
    private fun DrawRect(
        color: Color,
        heightPxProvider: () -> Float,
        modifier: Modifier = Modifier
    ) {
        val density = LocalDensity.current

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(with(density) {
                    heightPxProvider().toDp()
                })
        ) {
            drawRect(
                color = color,
                size = Size(size.width, size.height)
            )
        }
    }
}