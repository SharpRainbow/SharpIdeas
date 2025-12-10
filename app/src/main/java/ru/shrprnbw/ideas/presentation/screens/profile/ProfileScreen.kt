@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onEditProfile: () -> Unit = {}
) {

    val state = viewModel.state.collectAsState()
    val refreshState = rememberPullToRefreshState()

    when (val currentState = state.value) {

        is ProfileScreenState.Displaying -> {
            Scaffold(
                modifier = modifier
                    .fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Профиль",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(onClick = onBackClicked),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    )
                }
            ) { innerPadding ->
                PullToRefreshBox(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    state = refreshState,
                    isRefreshing = currentState.isRefreshing,
                    onRefresh = {
                        viewModel.processCommand(ProfileScreenCommand.RefreshProfile)
                    },
                    indicator = {
                        PullToRefreshDefaults.LoadingIndicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            state = refreshState,
                            isRefreshing = currentState.isRefreshing
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = currentState.email, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
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
                            text = "Привет, ${currentState.firstName}!",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = onEditProfile) {
                            Text("Изменить данные")
                        }
                        Spacer(Modifier.height(4.dp))
                        TextButton(onClick = {
                            viewModel.processCommand(ProfileScreenCommand.Logout)
                        }) {
                            Text("Выйти из аккаунта")
                        }
                    }
                }
            }
        }

        is ProfileScreenState.Error -> {
            val context = LocalContext.current
            LaunchedEffect(currentState.message) {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            }
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ошибка загрузки профиля",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(4.dp))
                OutlinedButton(onClick = {
                    viewModel.processCommand(ProfileScreenCommand.Logout)
                }) {
                    Text("Выйти из аккаунта")
                }
            }
        }

        ProfileScreenState.GoToEditProfile -> {

        }

        ProfileScreenState.Logout -> {
            LaunchedEffect(Unit) {
                onLogOut()
            }
        }
    }

}