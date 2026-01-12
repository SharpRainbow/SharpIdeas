@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens.transcription_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.TranscriptionSegment
import ru.shrprnbw.ideas.utils.DateFormatter
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun TranscriptionDetailScreen(
    modifier: Modifier = Modifier,
    noteId: String,
    transcriptionId: String,
    onBackClicked: () -> Unit,
    viewModel: TranscriptionDetailViewModel = hiltViewModel(
        creationCallback = { factory: TranscriptionDetailViewModel.Factory ->
            factory.create(noteId, transcriptionId)
        }
    )
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.transcription),
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
        }
    ) { innerPadding ->
        when (val currentState = state) {
            is TranscriptionDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator()
                }
            }

            is TranscriptionDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(currentState.errorMessage),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is TranscriptionDetailState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (currentState.transcription.autoGenerated) {
                                    stringResource(R.string.auto_generated_transcription)
                                } else {
                                    stringResource(R.string.manual_transcription)
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = DateFormatter.formatDateToString(currentState.transcription.createdAt),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(currentState.transcription.segments) { segment ->
                        TranscriptionSegmentItem(segment = segment)
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TranscriptionSegmentItem(
    modifier: Modifier = Modifier,
    segment: TranscriptionSegment
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = segment.speaker,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Utils.generateColorContrast(segment.speaker)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = segment.start,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "-",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = segment.end,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = segment.text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}
