package nrr.konnekt.feature.authentication

import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import nrr.konnekt.core.designsystem.component.Button
import nrr.konnekt.core.designsystem.component.SecureShadowedTextField
import nrr.konnekt.core.designsystem.component.ShadowedTextField
import nrr.konnekt.core.designsystem.theme.KonnektTheme
import nrr.konnekt.core.designsystem.theme.RubikIso
import nrr.konnekt.core.designsystem.util.KonnektIcon
import nrr.konnekt.core.designsystem.util.TextFieldErrorIndicator
import nrr.konnekt.core.ui.compositionlocal.LocalSnackbarHostState
import nrr.konnekt.core.ui.util.Side
import nrr.konnekt.core.ui.util.topRadialGradient

private val textFieldMaxWidth = 400.dp
private val textFieldsSpace = 16.dp
private val textFieldModifier = Modifier
    .sizeIn(maxWidth = textFieldMaxWidth)

@Composable
internal fun AuthenticationScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val animationSpec = tween<Color>(durationMillis = 1000)
    val topGradientColor by animateColorAsState(
        targetValue = if (viewModel.isSignIn) MaterialTheme.colorScheme.primary
            else Color.Transparent,
        animationSpec = animationSpec
    )
    val topRightGradientColor by animateColorAsState(
        targetValue = if (viewModel.isSignIn) Color.Transparent
            else MaterialTheme.colorScheme.primary,
        animationSpec = animationSpec
    )

    LaunchedEffect(viewModel.actionState) {
        when (viewModel.actionState) {
            is ActionState.Success -> {
                if (viewModel.isSignIn) snackbarHostState.showSnackbar(
                    message = "Logged in"
                )
            }
            is ActionState.Error -> {
                snackbarHostState.showSnackbar(
                    message = (viewModel.actionState as ActionState.Error).message,
                    withDismissAction = true
                )
            }
            else -> Unit
        }
        if (viewModel.actionState is ActionState.Error) viewModel.actionState = null
    }
    AuthenticationScreen(
        isSignIn = viewModel.isSignIn,
        onIsSignInChange = { viewModel.isSignIn = it },
        actionEnabled = viewModel.actionEnabled,
        email = viewModel.email,
        username = viewModel.username,
        password = viewModel.password,
        confirmPassword = viewModel.confirmPassword,
        onEmailChange = { viewModel.email = it },
        onUsernameChange = { viewModel.username = it },
        onPasswordChange = { viewModel.password = it },
        onConfirmPasswordChange = { viewModel.confirmPassword = it },
        onActionClick = {
            keyboardController?.hide()
            if (viewModel.isSignIn) viewModel.login()
            else viewModel.register()
        },
        verificationEmailSent = viewModel.verificationEmailSent,
        backToLogin = {
            viewModel.isSignIn = true
            viewModel.verificationEmailSent = false
            viewModel.actionState = null
        },
        modifier = modifier
            .topRadialGradient(
                color = topRightGradientColor,
                side = Side.RIGHT
            )
            .topRadialGradient(topGradientColor)
            .padding(contentPadding)
    )
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
    onActionClick: () -> Unit,
    verificationEmailSent: Boolean,
    backToLogin: () -> Unit,
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
                        placeholder = "Email",
                        errorIndicators = listOf(
                            TextFieldErrorIndicator(
                                error = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(
                                    email
                                ).matches(),
                                message = "Invalid Email"
                            )
                        )
                    )
                    AnimatedVisibility(
                        visible = !isSignIn
                    ) {
                        ShadowedTextField(
                            value = username,
                            onValueChange = onUsernameChange,
                            modifier = textFieldModifier,
                            placeholder = "Username",
                            errorIndicators = listOf(
                                TextFieldErrorIndicator(
                                    error = username.isNotBlank() && username.length < 5,
                                    message = "Username must be at least 5 characters"
                                )
                            )
                        )
                    }
                    SecureShadowedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        modifier = textFieldModifier,
                        placeholder = "Password",
                        errorIndicators = if (!isSignIn) listOf(
                            TextFieldErrorIndicator(
                                error = password.isNotBlank() && password.length < 6,
                                message = "Password must be at least 6 characters"
                            )
                        ) else null
                    )
                    AnimatedVisibility(
                        visible = !isSignIn
                    ) {
                        SecureShadowedTextField(
                            value = confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            modifier = textFieldModifier,
                            placeholder = "Confirm Password",
                            errorIndicators = listOf(
                                TextFieldErrorIndicator(
                                    error = confirmPassword.isNotBlank() && password != confirmPassword,
                                    message = "Passwords do not match"
                                )
                            )
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
                    onActionClick = onActionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End)
                )
            } else VerificationEmailSent(backToLogin)
        }
    }
}

@Composable
private fun VerificationEmailSent(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                text = "Verification email has\n been sent",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = onLoginClick,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text("Login")
            Icon(
                painter = painterResource(KonnektIcon.arrowRight),
                contentDescription = "login",
                modifier = Modifier.padding(start = 4   .dp)
            )
        }
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
    var confirmationEmailSent by remember { mutableStateOf(false) }

    KonnektTheme {
        Scaffold {
            AuthenticationScreen(
                isSignIn = isSignIn,
                onIsSignInChange = { b -> isSignIn = b },
                actionEnabled = password.isNotBlank(),
                email = "",
                username = "",
                password = password,
                confirmPassword = "",
                onEmailChange = {},
                onUsernameChange = {},
                onPasswordChange = { p -> password = p },
                onConfirmPasswordChange = {},
                onActionClick = {
                    confirmationEmailSent = true
                },
                verificationEmailSent = confirmationEmailSent,
                backToLogin = { confirmationEmailSent = false },
                modifier = Modifier
                    .padding(it)
                    .padding(32.dp)
            )
        }
    }
}