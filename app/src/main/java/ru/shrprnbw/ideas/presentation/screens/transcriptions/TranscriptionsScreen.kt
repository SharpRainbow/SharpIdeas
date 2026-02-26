@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.transcriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.presentation.screens.PagedItemsTemplate
import ru.shrprnbw.ideas.presentation.screens.SwipeToDeleteBox
import ru.shrprnbw.ideas.presentation.ui.theme.AudioGreen
import ru.shrprnbw.ideas.utils.DateFormatter
import ru.shrprnbw.ideas.utils.Utils.getStatusColor

@Composable
fun TranscriptionsScreen(
    modifier: Modifier = Modifier,
    noteId: String,
    hasAccess: Boolean,
    onBackClicked: () -> Unit,
    onTranscriptionClicked: (String) -> Unit,
    viewModel: TranscriptionsViewModel = hiltViewModel(
        creationCallback = { factory: TranscriptionsViewModel.Factory ->
            factory.create(noteId, hasAccess)
        }
    )
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val refreshState = rememberPullToRefreshState()
    val transcriptions = viewModel.transcriptions.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage = state.error?.let { stringResource(it) }
    LaunchedEffect(state.error) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.processCommand(TranscriptionsCommand.ResetError)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.transcriptions),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBackClicked
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.canCreateTranscriptions) {
                FloatingActionButton(
                    onClick = {
                        viewModel.processCommand(
                            TranscriptionsCommand.ShowAddTranscriptionDialog(true)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = stringResource(R.string.transcription_headline)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state = refreshState,
            isRefreshing = transcriptions.loadState.refresh is LoadState.Loading,
            onRefresh = {
                transcriptions.refresh()
            },
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = refreshState,
                    isRefreshing = transcriptions.loadState.refresh is LoadState.Loading
                )
            }
        )  {
            PagedItemsTemplate(
                items = transcriptions,
                paddingValues = PaddingValues(),
                errorText = stringResource(R.string.error_network),
                keyProducer = { index -> transcriptions[index]?.id ?: index },
                emptyListPlaceholder = {
                    Text(
                        text = stringResource(R.string.transcriptions_list_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                },
                showLoading = false
            ) { transcription ->
                SwipeToDeleteBox(
                    onDelete = {
                        viewModel.processCommand(
                            TranscriptionsCommand.DeleteTranscription(transcription.id)
                        )
                    }
                ) {
                    TranscriptionItem(
                        transcription = transcription,
                        onClick = { onTranscriptionClicked(transcription.id) }
                    )
                }
            }

            if (state.showAddTranscriptionDialog) {
                AddTranscriptionDialog(
                    onDismissRequest = {
                        viewModel.processCommand(
                            TranscriptionsCommand.ShowAddTranscriptionDialog(false)
                        )
                    },
                    onConfirmClick = {
                        viewModel.processCommand(
                            TranscriptionsCommand.AddTranscriptionTask
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TranscriptionItem(
    modifier: Modifier = Modifier,
    transcription: Transcription,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (transcription.autoGenerated) {
                    stringResource(R.string.auto_generated_transcription)
                } else {
                    stringResource(R.string.manual_transcription)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = DateFormatter.formatDateToString(transcription.createdAt),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (transcription.generationStatus.isNotBlank()) {
                Text(
                    text = stringResource(R.string.status) + stringResource(R.string.status_separator) + transcription.generationStatus,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = getStatusColor(transcription.generationStatus)
                )
            }
        }
    }
}

@Composable
private fun AddTranscriptionDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Filled.GraphicEq,
                contentDescription = null,
                tint = AudioGreen
            )
        },
        title = {
            Text(
                text = stringResource(R.string.transcription_headline),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(R.string.transcription_supporting_text),
                style = TextStyle(lineBreak = LineBreak.Simple)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(stringResource(R.string.accept))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
