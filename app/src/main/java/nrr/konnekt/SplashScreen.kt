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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nrr.konnekt.designsystem.theme.DarkGray

@Composable
internal fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = remember { Animatable(DarkGray) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val iconColor = remember { Animatable(DarkGray) }
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val width = with(density) {
        config.screenWidthDp.dp.toPx()
    }
    val height = with(density) {
        config.screenHeightDp.dp.toPx()
    }
    val rememberOnSplashFinished by rememberUpdatedState(onSplashFinished)
    var contentVisible by remember { mutableStateOf(true) }
    val exitDuration = 500

    LaunchedEffect(Unit) {
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

    AnimatedVisibility(
        visible = contentVisible,
        exit = fadeOut(tween(exitDuration))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                // bottom gradient
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(color.value, Color.Transparent),
                        center = Offset(
                            x = width * (4f / 5f),
                            y = height * (5.5f / 6f)
                        )
                    ),
                    alpha = 0.5f
                )
                // top gradient
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(color.value, Color.Transparent),
                        center = Offset(
                            x = width * (1f / 5f),
                            y = height * (1f / 6f)
                        )
                    ),
                    alpha = 0.4f
                )
        ) {
            Icon(
                painter = painterResource(id = nrr.konnekt.core.designsystem.R.drawable.konnekt),
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