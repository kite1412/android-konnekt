package nrr.konnekt.authentication

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nrr.konnekt.core.designsystem.component.Button
import nrr.konnekt.core.designsystem.component.SecureShadowedTextField
import nrr.konnekt.core.designsystem.component.ShadowedTextField
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.RubikIso
import nrr.konnekt.core.designsystem.util.KonnektIcon

private val textFieldMaxWidth = 400.dp
private val textFieldsSpace = 16.dp
private val textFieldModifier = Modifier
    .sizeIn(maxWidth = textFieldMaxWidth)

@Composable
internal fun AuthenticationScreen(
    viewModel: AuthenticationViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AuthenticationScreen(
    isSignIn: Boolean,
    onIsSignInChange: (Boolean) -> Unit,
    actionEnabled: Boolean,
    email: String,
    username: String,
    password: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    verificationEmailSent: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(verificationEmailSent) { state ->
            if (!state) Column(
                modifier = Modifier
                    .sizeIn(maxWidth = textFieldMaxWidth)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
            ) {
                AnimatedContent(
                    targetState = isSignIn,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) {
                    Text(
                        text = if (it) "Sign In" else "Sign Up",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = RubikIso
                        )
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(textFieldsSpace)
                ) {
                    ShadowedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        modifier = textFieldModifier,
                        placeholder = "Email"
                    )
                    AnimatedVisibility(
                        visible = !isSignIn
                    ) {
                        ShadowedTextField(
                            value = username,
                            onValueChange = onUsernameChange,
                            modifier = textFieldModifier,
                            placeholder = "Username"
                        )
                    }
                    SecureShadowedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        modifier = textFieldModifier,
                        placeholder = "Password"
                    )
                    AnimatedVisibility(
                        visible = !isSignIn
                    ) {
                        SecureShadowedTextField(
                            value = confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            modifier = textFieldModifier,
                            placeholder = "Confirm Password"
                        )
                    }
                }
                Mode(
                    mode = if (isSignIn) "New Account" else "Login",
                    action = if (isSignIn) "Login" else "Register",
                    actionEnabled = actionEnabled,
                    onModeClick = {
                        onIsSignInChange(!isSignIn)
                    },
                    onActionClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)
                )
            } else VerificationEmailSent()
        }
    }
}

@Composable
private fun VerificationEmailSent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(KonnektIcon.mailCheck),
            contentDescription = "email verification sent",
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .sizeIn(
                    maxHeight = 80.dp,
                    maxWidth = 80.dp
                )
                .fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Verification email has been sent to you",
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Mode(
    mode: String,
    action: String,
    actionEnabled: Boolean,
    onModeClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onModeClick
        ) {
            Text(
                text = mode
            )
        }
        Button(
            onClick = onActionClick,
            enabled = actionEnabled
        ) {
            Text(
                text = action
            )
        }
    }
}

@Preview
@Composable
private fun AuthenticationScreenPreview() {
    var isSignIn by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }

    KonnektTheme {
        Scaffold {
            AuthenticationScreen(
                isSignIn = isSignIn,
                onIsSignInChange = { b -> isSignIn = b },
                actionEnabled = false,
                email = "",
                username = "",
                password = password,
                confirmPassword = "",
                onEmailChange = {},
                onUsernameChange = {},
                onPasswordChange = { p -> password = p },
                onConfirmPasswordChange = {},
                modifier = Modifier
                    .padding(it)
                    .padding(32.dp),
                verificationEmailSent = true
            )
        }
    }
}