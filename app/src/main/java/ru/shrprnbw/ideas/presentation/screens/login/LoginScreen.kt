@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
                    viewModel.processCommand(
                        LoginCommand.ResetErrorMessage
                    )
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
                            contentDescription = stringResource(R.string.app_icon_description)
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
                                append(stringResource(R.string.welcome_title))
                            }
                            append(stringResource(R.string.login_title))
                        },
                        textAlign = TextAlign.Center,
                        style = TextStyle(lineHeight = 24.sp, platformStyle = PlatformTextStyle(includeFontPadding = false))
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        UserInfoFieldNextFocus(
                            value = currentState.serverUrl,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    LoginCommand.InputServerUrl(text)
                                )
                            },
                            label = stringResource(R.string.server_url_hint),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Default.Dns,
                                    contentDescription = stringResource(R.string.server_icon_description)
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        UserInfoFieldNextFocus(
                            value = currentState.email,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    LoginCommand.InputEmail(text)
                                )
                            },
                            label = stringResource(R.string.email_hint),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = stringResource(R.string.email_icon_description)
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PasswordInputField(
                            value = currentState.password,
                            placeHolderText = stringResource(R.string.password_hint)
                        ) { text ->
                            viewModel.processCommand(
                                LoginCommand.InputPassword(text)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontSize = 12.sp)) {
                                    append(stringResource(R.string.create_account_hint))
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
                                    append(stringResource(R.string.register))
                                }
                            },
                            textAlign = TextAlign.Center,
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        enabled = currentState.isLoginEnabled,
                        onClick = {
                            viewModel.processCommand(LoginCommand.Login)
                        }
                    ) {
                        Text(stringResource(R.string.enter))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = stringResource(R.string.or_divider),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        onClick = {
                            viewModel.processCommand(
                                LoginCommand.LoginWithGoogle(
                                    context
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSystemInDarkTheme()) Color(0xFF131314) else Color.White,
                            contentColor = if (isSystemInDarkTheme()) Color(0xFFE3E3E3) else Color(0xFF1F1F1F)
                        ),
                        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF8E918F) else Color(0xFF747775)),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_google_logo),
                            contentDescription = stringResource(R.string.google_icon_description),
                            modifier = Modifier.size(ButtonDefaults.MediumIconSize)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.enter_with_google),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        onClick = {
                            viewModel.processCommand(
                                LoginCommand.LoginWithYandex(
                                    context
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                        )
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_yandex_logo),
                            contentDescription = stringResource(R.string.yandex_icon_description),
                            modifier = Modifier.size(ButtonDefaults.MediumIconSize)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.enter_with_yandex),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
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