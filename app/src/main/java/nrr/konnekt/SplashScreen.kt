package nrr.konnekt

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nrr.konnekt.core.designsystem.theme.DarkGray
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.ui.util.bottomRadialGradient
import nrr.konnekt.core.ui.util.topRadialGradient

@Composable
internal fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
    startAnimation: Boolean = true
) {
    val color = remember { Animatable(DarkGray) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val iconColor = remember { Animatable(DarkGray) }
    val rememberOnSplashFinished by rememberUpdatedState(onSplashFinished)
    var contentVisible by remember { mutableStateOf(true) }
    val exitDuration = 500

    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            launch {
                iconColor.animateTo(
                    targetValue = primaryColor,
                    animationSpec = tween(durationMillis = 1500)
                )
                contentVisible = false
                delay(exitDuration.toLong())
                rememberOnSplashFinished()
            }
            color.animateTo(
                targetValue = primaryColor,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    AnimatedVisibility(
        visible = contentVisible,
        exit = fadeOut(tween(exitDuration))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .bottomRadialGradient(color.value)
                .topRadialGradient(color.value)
        ) {
            Icon(
                painter = painterResource(id = KonnektIcon.logo),
                contentDescription = "Icon Logo",
                modifier = Modifier
                    .align(Alignment.Center)
                    .sizeIn(
                        maxWidth = (267 * 2).dp,
                        maxHeight = (47 * 2).dp
                    )
                    .fillMaxSize()
                    .padding(horizontal = 64.dp),
                tint = iconColor.value
            )
        }
    }
}