@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)

package ru.shrprnbw.ideas.presentation.screens.keywords

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.paging.compose.collectAsLazyPagingItems
import ru.shrprnbw.ideas.R
import ru.shrprnbw.ideas.domain.entity.Keyword
import ru.shrprnbw.ideas.presentation.screens.PagedItemsTemplate
import ru.shrprnbw.ideas.presentation.ui.theme.KeywordPurple
import ru.shrprnbw.ideas.utils.DateFormatter
import ru.shrprnbw.ideas.utils.Utils.getStatusColor

@Composable
fun KeywordsScreen(
    modifier: Modifier = Modifier,
    noteId: String,
    hasAccess: Boolean,
    onBackClicked: () -> Unit,
    viewModel: KeywordsViewModel = hiltViewModel(
        creationCallback = { factory: KeywordsViewModel.Factory ->
            factory.create(noteId, hasAccess)
        }
    )
) {

    val state by viewModel.state.collectAsState()
    val keywords = viewModel.keywords.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage = state.error?.let { stringResource(it) }
    LaunchedEffect(state.error) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.processCommand(KeywordsCommand.ResetError)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.keywords),
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
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.canCreateKeywords) {
                FloatingActionButton(
                    onClick = {
                        viewModel.processCommand(
                            KeywordsCommand.ShowAddKeywordsDialog(true)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = stringResource(R.string.keywords)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            PagedItemsTemplate(
                items = keywords,
                paddingValues = innerPadding,
                errorText = stringResource(R.string.error_network),
                keyProducer = { index -> keywords[index]?.id ?: index },
                screenContent = {},
            ) { keyword ->
                KeywordItem(
                    keyword = keyword,
                    onClick = {
                        viewModel.processCommand(
                            KeywordsCommand.ShowKeywordChipsDialog(keyword)
                        )
                    }
                )
            }

            if (state.showKeywordChipsDialog && state.selectedKeyword != null) {
                KeywordChipsDialog(
                    keyword = state.selectedKeyword!!,
                    addedKeywords = state.addedKeywords,
                    onDismissRequest = {
                        viewModel.processCommand(KeywordsCommand.DismissKeywordChipsDialog)
                    },
                    onKeywordClick = { keywordText, isAdded ->
                        if (!isAdded) {
                            viewModel.processCommand(
                                KeywordsCommand.AddKeywordAsTag(keywordText)
                            )
                        } else {
                            viewModel.processCommand(
                                KeywordsCommand.RemoveNoteTag(keywordText)
                            )
                        }
                    }
                )
            }

            if (state.showAddKeywordsDialog) {
                AddKeywordsDialog(
                    onDismissRequest = {
                        viewModel.processCommand(
                            KeywordsCommand.ShowAddKeywordsDialog(false)
                        )
                    },
                    onConfirmClick = {
                        viewModel.processCommand(
                            KeywordsCommand.AddKeywordsTask
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun KeywordItem(
    modifier: Modifier = Modifier,
    keyword: Keyword,
    onClick: () -> Unit = {}
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
                text = keyword.keyword.ifBlank { stringResource(R.string.keywords_placeholder) },
                fontSize = 16.sp,
                fontWeight = if (keyword.keyword.isNotBlank()) FontWeight.SemiBold
                else FontWeight.Normal,
                color = if (keyword.keyword.isNotBlank()) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )

            Text(
                text = DateFormatter.formatDateToString(keyword.createdAt),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (keyword.generationStatus.isNotBlank()) {
                Text(
                    text = stringResource(R.string.status) + stringResource(R.string.status_separator) + keyword.generationStatus,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = getStatusColor(keyword.generationStatus)
                )
            }
        }
    }
}

@Composable
private fun AddKeywordsDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Label,
                contentDescription = null,
                tint = KeywordPurple
            )
        },
        title = {
            Text(
                text = stringResource(R.string.keywords),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(R.string.keywords_supporting_text),
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

@Composable
private fun KeywordChipsDialog(
    modifier: Modifier = Modifier,
    keyword: Keyword,
    addedKeywords: Set<String>,
    onDismissRequest: () -> Unit,
    onKeywordClick: (String, Boolean) -> Unit
) {
    val keywordList = keyword.keyword.split(",").map { it.trim() }.filter { it.isNotBlank() }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Label,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = stringResource(R.string.add_keywords_to_note),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.keywords_dialog_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    keywordList.forEach { keywordText ->
                        val isAdded = addedKeywords.contains(keywordText)
                        FilterChip(
                            selected = isAdded,
                            onClick = {
                                onKeywordClick(keywordText, isAdded)
                            },
                            label = { Text(keywordText) },
                            leadingIcon = {
                                AnimatedVisibility(
                                    visible = isAdded,
                                    enter = expandHorizontally(expandFrom = Alignment.Start),
                                    exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Done,
                                        contentDescription = stringResource(R.string.selected_icon_description),
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
