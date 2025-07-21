package nrr.konnekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import nrr.konnekt.designsystem.theme.KonnektTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel by viewModels<KonnektViewModel>()
        setContent {
            KonnektTheme {
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
                ) {
                    if (!it) SplashScreen(
                        onSplashFinished = { viewModel.showSplashOnce = true }
                    ) else {
                        // TODO replace with nav tree
                        Scaffold { innerPadding ->
                            Column(modifier = Modifier.padding(innerPadding)) {
                                repeat(50) {
                                    Text("Hello")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}