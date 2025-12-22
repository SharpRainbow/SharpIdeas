@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.transcriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Transcription
import ru.shrprnbw.ideas.presentation.screens.PagedItemsTemplate
import ru.shrprnbw.ideas.utils.DateFormatter

@Composable
private fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "SCHEDULED" -> Color(0xFFFF9800) // Orange
        "IN_PROGRESS" -> Color(0xFF2196F3) // Blue
        "COMPLETED" -> Color(0xFF4CAF50) // Green
        "FAILED" -> Color(0xFFF44336) // Red
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
fun TranscriptionsScreen(
    modifier: Modifier = Modifier,
    noteId: String,
    onBackClicked: () -> Unit,
    onTranscriptionClicked: (String) -> Unit,
    viewModel: TranscriptionsViewModel = hiltViewModel(
        creationCallback = { factory: TranscriptionsViewModel.Factory ->
            factory.create(noteId)
        }
    )
) {
    val transcriptions = viewModel.transcriptions.collectAsLazyPagingItems()

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
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable(onClick = onBackClicked),
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            )
        }
    ) { innerPadding ->
        PagedItemsTemplate(
            items = transcriptions,
            paddingValues = innerPadding,
            errorText = stringResource(R.string.error_network)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transcriptions.itemCount) { index ->
                    transcriptions[index]?.let { transcription ->
                        TranscriptionItem(
                            transcription = transcription,
                            onClick = { onTranscriptionClicked(transcription.id) }
                        )
                    }
                }

                if (transcriptions.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ContainedLoadingIndicator()
                        }
                    }
                }
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
                    text = stringResource(R.string.status) + ": ${transcription.generationStatus}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = getStatusColor(transcription.generationStatus)
                )
            }
        }
    }
}
