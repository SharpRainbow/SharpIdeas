@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ru.shrprnbw.ideas.presentation.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import ru.shrprnbw.ideas.presentation.ui.theme.AudioGreen
import ru.shrprnbw.ideas.presentation.ui.theme.TextBlue
import ru.shrprnbw.ideas.utils.Utils

@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    value: String,
    placeHolderText: String = "Password",
    insufficientChars: Boolean = false,
    wrongPassword: Boolean = false,
    onValueChange: (String) -> Unit,
) {

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(placeHolderText) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Filled.Password,
                contentDescription = "Password Icon"
            )
        },
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(
                onClick = { passwordVisible = !passwordVisible }
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = image,
                    contentDescription = description
                )
            }
        },
        singleLine = true,
        isError = insufficientChars || wrongPassword,
        supportingText = {
            if (insufficientChars) Text(text = "Password must be at least 8 characters long")
            else if (wrongPassword) Text(text = "Wrong password")
        }
    )
}

@Composable
fun UserInfoFieldNextFocus(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = icon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    )
}

@Composable
fun UserInfoFieldEditDone(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable () -> Unit
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = icon
    )
}

@Composable
fun <T : Any> PagedItemsTemplate(
    items: LazyPagingItems<T>,
    paddingValues: PaddingValues,
    errorText: String,
    listPadding: Dp = 16.dp,
    listContentPadding: PaddingValues = PaddingValues(vertical = 12.dp),
    listVerticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    showLoading: Boolean = true,
    keyProducer: ((Int) -> Any)? = null,
    listState: LazyListState = rememberLazyListState(),
    screenContent: @Composable () -> Unit,
    itemContent: @Composable (item: T) -> Unit
) {
    when {
        items.loadState.refresh is LoadState.Loading && showLoading && items.itemCount == 0 -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                ContainedLoadingIndicator()
            }
        }

        items.loadState.refresh is LoadState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                LaunchedEffect(Unit) {
                    Log.d("PagedItemsTemplate", "Error loading items: ${(items.loadState.refresh as LoadState.Error).error.message}")
                }
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = listPadding),
                contentPadding = listContentPadding,
                verticalArrangement = listVerticalArrangement,
                state = listState
            ) {
                item {
                    screenContent()
                }

                items(items.itemCount, keyProducer) { index ->
                    items[index]?.let { item ->
                        itemContent(item)
                    }
                }

                if (items.loadState.append is LoadState.Loading) {
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

enum class NoteType { Text, Audio }

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    title: String,
    preview: String,
    time: String,
    type: NoteType,
    tags: List<String> = emptyList<String>(),
    onNoteClicked: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onNoteClicked),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                if (preview.isNotBlank()) {
                    Text(
                        text = preview,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (type == NoteType.Text) {
                            NoteLabel(
                                label = "Текст",
                                color = TextBlue,
                                imageVector = Icons.Rounded.TextFields
                            )
                        } else {
                            NoteLabel(
                                label = "Аудио",
                                color = AudioGreen,
                                imageVector = Icons.Rounded.AudioFile
                            )
                        }
                        for (t in tags) {
                            NoteLabel(
                                label = t,
                                color = Utils.generateColorContrast(t)
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun NoteLabel(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    imageVector: ImageVector? = null
) {
    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(4.dp))
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(label, color = color, fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
    }
}

@Composable
fun NavigationDrawerContent(
    modifier: Modifier = Modifier,
    selectedScreen: NavigationScreen = NavigationScreen.Home,
    onHomeClicked: () -> Unit = {},
    onTagManagementClicked: () -> Unit = {},
    onGroupManagementClicked: () -> Unit = {}
) {
    ModalDrawerSheet(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SharpIdeas",
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
            label = { Text("Главный экран") },
            selected = selectedScreen == NavigationScreen.Home,
            onClick = onHomeClicked,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Rounded.Label, contentDescription = null) },
            label = { Text("Управление тегами") },
            selected = selectedScreen == NavigationScreen.TagManagement,
            onClick = onTagManagementClicked,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Group, contentDescription = null) },
            label = { Text("Управление группами") },
            selected = selectedScreen == NavigationScreen.GroupManagement,
            onClick = onGroupManagementClicked,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

enum class NavigationScreen {
    Home,
    TagManagement,
    GroupManagement
}