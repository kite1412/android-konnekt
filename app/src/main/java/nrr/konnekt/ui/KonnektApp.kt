package nrr.konnekt.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import nrr.konnekt.KonnektViewModel
import nrr.konnekt.SplashScreen
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.ui.compositionlocal.LocalFileCache
import nrr.konnekt.core.ui.compositionlocal.LocalFileNameFormatter
import nrr.konnekt.core.ui.compositionlocal.LocalFileResolver
import nrr.konnekt.core.ui.compositionlocal.LocalSnackbarHostState
import nrr.konnekt.core.ui.compositionlocal.SnackbarHostStateWrapper

@Composable
internal fun KonnektApp(
    modifier: Modifier = Modifier,
    viewModel: KonnektViewModel = hiltViewModel()
) {
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()
    val snackbarHostState = remember {
        SnackbarHostStateWrapper(
            coroutineScope = viewModel.viewModelScope
        )
    }
    val navController = rememberNavController()

    KonnektTheme {
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState,
            LocalFileResolver provides viewModel.fileResolver,
            LocalFileNameFormatter provides viewModel.fileNameFormatter,
            LocalFileCache provides viewModel.fileCache
        ) {
            AnimatedContent(
                targetState = viewModel.showSplashOnce,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(300)
                    ) { it } + fadeIn(tween(durationMillis = 200)) togetherWith
                            ExitTransition.None
                }
            ) { showContent ->
                if (!showContent) SplashScreen(
                    onSplashFinished = {
                        viewModel.showSplashOnce = true
                    },
                    startAnimation = isSignedIn != null
                ) else {
                    Scaffold(modifier = modifier) { p ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .consumeWindowInsets(p)
                        ) {
                            isSignedIn?.let {
                                KonnektNavHost(
                                    isSignedIn = it,
                                    scaffoldPadding = p,
                                    navController = navController
                                )
                            }
                            SnackbarHost(
                                hostState = snackbarHostState.snackbarHostState,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(p),
                                snackbar = { KonnektSnackbar(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}