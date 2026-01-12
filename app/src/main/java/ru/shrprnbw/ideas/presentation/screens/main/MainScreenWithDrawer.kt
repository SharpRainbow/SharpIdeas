package ru.shrprnbw.ideas.presentation.screens.main

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
    navController: NavController,
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
                            navController.navigate(Screen.Notes.route) {
                                popUpTo(Screen.Notes.route) { inclusive = true }
                            }
                        }
                    } else {
                        scope.launch { drawerState.close() }
                    }
                },
                onTagManagementClicked = {
                    if (currentRoute != Screen.Tags) {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.Tags.route)
                        }
                    } else {
                        scope.launch { drawerState.close() }
                    }
                },
                onGroupManagementClicked = {
                    if (currentRoute != Screen.Groups) {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.Groups.route)
                        }
                    } else {
                        scope.launch { drawerState.close() }
                    }
                },
                onSharedNotesClicked = {
                    if (currentRoute != Screen.SharedNotes) {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.SharedNotes.route)
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
