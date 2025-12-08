package ru.shrprnbw.ideas.presentation.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.presentation.screens.PasswordInputField
import ru.shrprnbw.ideas.presentation.screens.UserInfoFieldNextFocus

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoggedIn: () -> Unit = {},
    onRegisterClicked: () -> Unit = {}
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is LoginScreenState.InputData -> {
            LaunchedEffect(
                currentState.errorMessage
            ) {
                currentState.errorMessage?.let {
                    Toast.makeText(context, currentState.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            Scaffold(
                modifier = modifier.fillMaxSize()
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    ) {
                        if (currentState.isLoading) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "App launcher"
                        )
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = buildAnnotatedString {
                            withStyle(
                                style =
                                    SpanStyle(
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                            ) {
                                append("Добро пожаловать\n")
                            }
                            append("Войдите в свой аккаунт")
                        },
                        textAlign = TextAlign.Center,
                        style = TextStyle(lineHeight = 24.sp, platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        UserInfoFieldNextFocus(
                            value = currentState.email,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    LoginCommand.InputEmail(text)
                                )
                            },
                            label = "Электронная почта",
                            icon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = "Email Icon"
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PasswordInputField(
                            value = currentState.password,
                            placeHolderText = "Пароль"
                        ) { text ->
                            viewModel.processCommand(
                                LoginCommand.InputPassword(text)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontSize = 12.sp)) {
                                    append("Нет аккаунта?\n")
                                }
                                withLink(
                                    LinkAnnotation.Clickable(
                                        tag = "register",
                                        styles = TextLinkStyles(
                                            style = SpanStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        ),
                                        linkInteractionListener = {
                                            onRegisterClicked()
                                        }
                                    )
                                ) {
                                    append("Зарегистрироваться")
                                }
                            },
                            textAlign = TextAlign.Center,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        enabled = currentState.isLoginEnabled,
                        onClick = {
                            viewModel.processCommand(LoginCommand.Login)
                        }
                    ) {
                        Text("Войти")
                    }
                }
            }
        }

        is LoginScreenState.LoggedIn -> {
            LaunchedEffect(Unit) {
                onLoggedIn()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}