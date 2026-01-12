@file:OptIn(ExperimentalMaterial3Api::class)

package ru.shrprnbw.ideas.presentation.screens.register

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.presentation.screens.PasswordInputField
import ru.shrprnbw.ideas.presentation.screens.UserInfoFieldNextFocus

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onRegisterSuccess: () -> Unit
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val state = viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    when (val currentState = state.value) {
        is RegisterScreenState.Editing -> {
            LaunchedEffect(
                currentState.error
            ) {
                currentState.error?.let {
                    Toast.makeText(context, currentState.error, Toast.LENGTH_SHORT).show()
                }
            }
            Scaffold(
                modifier = modifier.fillMaxSize().imePadding(),
                topBar = {
                    RegisterScreenTopBar(
                        onBackClicked = onBackClicked,
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
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
                            .padding(top = 8.dp, bottom = 8.dp),
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
                                append(stringResource(R.string.register_title))
                            }
                            append(stringResource(R.string.register_subtitle))
                        },
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            lineHeight = 24.sp,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        )
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        UserInfoFieldNextFocus(
                            value = currentState.username,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    RegisterCommand.InputUsername(text)
                                )
                            },
                            label = stringResource(R.string.username_label),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Abc,
                                    contentDescription = stringResource(R.string.username_icon_description)
                                )
                            },
                        )
                        UserInfoFieldNextFocus(
                            value = currentState.firstName,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    RegisterCommand.InputFirstName(text)
                                )
                            },
                            label = stringResource(R.string.first_name_label),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = stringResource(R.string.name_icon_description)
                                )
                            },
                        )
                        UserInfoFieldNextFocus(
                            value = currentState.lastName,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    RegisterCommand.InputLastName(text)
                                )
                            },
                            label = stringResource(R.string.last_name_label),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = stringResource(R.string.surname_icon_description)
                                )
                            },
                        )
                        UserInfoFieldNextFocus(
                            value = currentState.email,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    RegisterCommand.InputEmail(text)
                                )
                            },
                            label = stringResource(R.string.email_label),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = stringResource(R.string.email_icon_description)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PasswordInputField(
                            value = currentState.password,
                            placeHolderText = stringResource(R.string.password_label),
                            insufficientChars = currentState.errorInputPassword
                        ) { text ->
                            viewModel.processCommand(
                                RegisterCommand.InputPassword(text)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        enabled = currentState.isSubmitEnabled,
                        onClick = {
                            viewModel.processCommand(RegisterCommand.Submit)
                        }
                    ) {
                        Text(stringResource(R.string.register_button))
                    }
                }

            }
        }

        RegisterScreenState.Registered -> {
            LaunchedEffect(Unit) {
                onRegisterSuccess()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreenTopBar(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onBackClicked
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back_icon_description)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

