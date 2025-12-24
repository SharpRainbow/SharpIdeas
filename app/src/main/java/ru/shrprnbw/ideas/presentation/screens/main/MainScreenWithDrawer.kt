package ru.shrprnbw.ideas.presentation.screens.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.presentation.navigation.Screen
import ru.shrprnbw.ideas.presentation.screens.NavigationDrawerContent
import ru.shrprnbw.ideas.presentation.screens.NavigationScreen

@Composable
fun MainScreenWithDrawer(
    modifier: Modifier = Modifier,
    selectedScreen: NavigationScreen,
    currentRoute: Screen,
    onNavigateToNotes: () -> Unit,
    onNavigateToTags: () -> Unit,
    gesturesEnabled: Boolean = true,
    content: @Composable (DrawerState, CoroutineScope) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            NavigationDrawerContent(
                selectedScreen = selectedScreen,
                onHomeClicked = {
                    if (currentRoute != Screen.Notes) {
                        scope.launch {
                            drawerState.close()
                            onNavigateToNotes()
                        }
                    } else {
                        scope.launch { drawerState.close() }
                    }
                },
                onTagManagementClicked = {
                    if (currentRoute != Screen.Tags) {
                        scope.launch {
                            drawerState.close()
                            onNavigateToTags()
                        }
                    } else {
                        scope.launch { drawerState.close() }
                    }
                }
            )
        }
    ) {
        content(drawerState, scope)
    }
}
