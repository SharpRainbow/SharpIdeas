@file:OptIn(ExperimentalMaterial3Api::class)

package ru.shrprnbw.ideas.presentation.screens.profile_edit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = hiltViewModel(),
    onEditFinished: () -> Unit = { }
) {

    val state = viewModel.state.collectAsState()
    val context = LocalContext.current

    when(val currentState = state.value) {

        is ProfileEditState.Editing -> {
            LaunchedEffect(
                currentState.error
            ) {
                currentState.error?.let {
                    Toast.makeText(context, currentState.error, Toast.LENGTH_SHORT).show()
                }
            }
            Scaffold(
                modifier = modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.profile_edit_title),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onEditFinished
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Utils.generateColor(currentState.firstName)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.firstName.firstOrNull()?.toString() ?: "U",
                            color = Color.White,
                            fontSize = 48.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${currentState.firstName} ${currentState.lastName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = currentState.email,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    ProfileEditCommand.InputEmail(text)
                                )
                            },
                            label = { Text(stringResource(R.string.email_label)) },
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = stringResource(R.string.email_icon_description)
                                )
                            },
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = currentState.username,
                            onValueChange = { text ->
                                viewModel.processCommand(
                                    ProfileEditCommand.InputUsername(text)
                                )
                            },
                            label = { Text(stringResource(R.string.username_label)) },
                            leadingIcon = {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Abc,
                                    contentDescription = stringResource(R.string.username_icon_description)
                                )
                            },
                        )
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        enabled = currentState.isSaveEnabled,
                        onClick = {
                            viewModel.processCommand(ProfileEditCommand.SaveProfile)
                        }
                    ) {
                        Text(stringResource(R.string.profile_save_button))
                    }
                }

            }
        }

        ProfileEditState.Finished -> {
            LaunchedEffect(Unit) {
                onEditFinished()
            }
        }
    }

}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ProfileEditScreenPreview() {
    ProfileEditScreen()
}