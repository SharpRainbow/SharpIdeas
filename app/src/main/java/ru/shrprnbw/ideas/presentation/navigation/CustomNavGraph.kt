@file:OptIn(ExperimentalSharedTransitionApi::class)

package ru.shrprnbw.ideas.presentation.navigation

import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import ru.shrprnbw.ideas.presentation.screens.NavigationScreen
import ru.shrprnbw.ideas.presentation.screens.login.LoginScreen
import ru.shrprnbw.ideas.presentation.screens.main.MainScreenWithDrawer
import ru.shrprnbw.ideas.presentation.screens.note_editor.NoteEditorScreen
import ru.shrprnbw.ideas.presentation.screens.notes_list.NoteListScreen
import ru.shrprnbw.ideas.presentation.screens.profile.ProfileScreen
import ru.shrprnbw.ideas.presentation.screens.profile_edit.ProfileEditScreen
import ru.shrprnbw.ideas.presentation.screens.register.RegisterScreen
import ru.shrprnbw.ideas.presentation.screens.search.SearchScreen
import ru.shrprnbw.ideas.presentation.screens.tag_management.TagManagementScreen
import ru.shrprnbw.ideas.presentation.screens.transcription_detail.TranscriptionDetailScreen
import ru.shrprnbw.ideas.presentation.screens.transcriptions.TranscriptionsScreen

private const val ANIMATION_DUR = 500
const val SEARCH_ANIMATION_DUR = 500
const val SEARCH_ANIMATION_KEY = "search_screen_search_bar"

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = NavigationGraph.Auth.route,
            enterTransition = {
                when (targetState.destination.route) {
                    Screen.Search.route -> fadeIn(animationSpec = tween(ANIMATION_DUR))
                    else -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(ANIMATION_DUR)
                    )
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Search.route -> fadeOut(animationSpec = tween(ANIMATION_DUR))
                    else -> fadeOut(animationSpec = tween(ANIMATION_DUR))
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Screen.Search.route -> fadeIn(animationSpec = tween(ANIMATION_DUR))
                    else -> fadeIn(animationSpec = tween(ANIMATION_DUR))
                }
            },
            popExitTransition = {
                when (initialState.destination.route) {
                    Screen.Search.route -> fadeOut(animationSpec = tween(ANIMATION_DUR))
                    else -> slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(ANIMATION_DUR)
                    )
                }
            }
        ) {
            navigation(
                startDestination = Screen.Login.route,
                route = NavigationGraph.Auth.route
            ) {
                composable(route = Screen.Login.route) {
                    LoginScreen(
                        onLoggedIn = {
                            navController.navigate(NavigationGraph.Main.route) {
                                popUpTo(NavigationGraph.Auth.route) { inclusive = true }
                            }
                        },
                        onRegisterClicked = {
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }

                composable(route = Screen.Register.route) {
                    RegisterScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onRegisterSuccess = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            navigation(
                startDestination = Screen.Notes.route,
                route = NavigationGraph.Main.route
            ) {
                composable(route = Screen.Notes.route) {
                    MainScreenWithDrawer(
                        selectedScreen = NavigationScreen.Home,
                        currentRoute = Screen.Notes,
                        onNavigateToNotes = {
                            navController.navigate(Screen.Notes.route) {
                                popUpTo(Screen.Notes.route) { inclusive = true }
                            }
                        },
                        onNavigateToTags = {
                            navController.navigate(Screen.Tags.route)
                        },
                        gesturesEnabled = true
                    ) { drawerState, scope ->
                        NoteListScreen(
                            animatedVisibilityScope = this@composable,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            onMenuClicked = {
                                scope.launch { drawerState.open() }
                            },
                            onSearchTriggered = {
                                navController.navigate(Screen.Search.route)
                            },
                            onProfileClicked = {
                                navController.navigate(Screen.UserInfo.route)
                            },
                            onNoteClicked = { noteId ->
                                navController.navigate(Screen.EditNote.createRoute(noteId))
                            }
                        )
                    }
                }

                composable(route = Screen.Search.route) {
                    SearchScreen(
                        animatedVisibilityScope = this@composable,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(route = Screen.Tags.route) {
                    MainScreenWithDrawer(
                        selectedScreen = NavigationScreen.TagManagement,
                        currentRoute = Screen.Tags,
                        onNavigateToNotes = {
                            navController.navigate(Screen.Notes.route) {
                                popUpTo(Screen.Notes.route) { inclusive = true }
                            }
                        },
                        onNavigateToTags = {
                            navController.navigate(Screen.Tags.route)
                        },
                        gesturesEnabled = true
                    ) { drawerState, scope ->
                        TagManagementScreen(
                            onMenuClicked = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }
                }

                composable(route = Screen.UserInfo.route) {
                    ProfileScreen(
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onLogOut = {
                            navController.navigate(NavigationGraph.Auth.route) {
                                popUpTo(NavigationGraph.Main.route) { inclusive = true }
                            }
                        },
                        onEditProfile = {
                            navController.navigate(Screen.EditUserInfo.route)
                        }
                    )
                }

                composable(route = Screen.EditUserInfo.route) {
                    ProfileEditScreen {
                        navController.popBackStack()
                    }
                }

                composable(
                    route = Screen.EditNote.route,
                    arguments = Screen.EditNote.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.EditNote.getNoteId(navBackStackEntry.arguments)
                    NoteEditorScreen(
                        noteId = noteId,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onTranscriptionsClicked = {
                            navController.navigate(Screen.Transcriptions.createRoute(noteId))
                        }
                    )
                }

                composable(
                    route = Screen.Transcriptions.route,
                    arguments = Screen.Transcriptions.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.Transcriptions.getNoteId(navBackStackEntry.arguments)
                    TranscriptionsScreen(
                        noteId = noteId,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onTranscriptionClicked = { transcriptionId ->
                            navController.navigate(
                                Screen.TranscriptionDetail.createRoute(noteId, transcriptionId)
                            )
                        }
                    )
                }

                composable(
                    route = Screen.TranscriptionDetail.route,
                    arguments = Screen.TranscriptionDetail.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.TranscriptionDetail.getNoteId(navBackStackEntry.arguments)
                    val transcriptionId =
                        Screen.TranscriptionDetail.getTranscriptionId(navBackStackEntry.arguments)
                    TranscriptionDetailScreen(
                        noteId = noteId,
                        transcriptionId = transcriptionId,
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

sealed class NavigationGraph(val route: String) {
    data object Auth : NavigationGraph("auth")
    data object Main : NavigationGraph("main")
}

sealed class Screen(val route: String) {
    data object Register : Screen("register")
    data object Login : Screen("login")
    data object Notes : Screen("notes")
    data object Search : Screen("search")
    data object Tags : Screen("tags")
    data object UserInfo : Screen("user_info")
    data object EditUserInfo : Screen("user_info_edit")

    data object EditNote : Screen("edit_note/{note_id}") {
        private const val noteIdArg = "note_id"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType }
        )

        fun createRoute(noteId: String): String {
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }
    }

    data object Transcriptions : Screen("transcriptions/{note_id}") {
        private const val noteIdArg = "note_id"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType }
        )

        fun createRoute(noteId: String): String {
            return "transcriptions/$noteId"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }
    }

    data object TranscriptionDetail : Screen("transcription/{note_id}/{transcription_id}") {
        private const val noteIdArg = "note_id"
        private const val transcriptionIdArg = "transcription_id"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType },
            navArgument(transcriptionIdArg) { type = NavType.StringType }
        )

        fun createRoute(noteId: String, transcriptionId: String): String {
            return "transcription/$noteId/$transcriptionId"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }

        fun getTranscriptionId(arguments: Bundle?): String {
            return arguments?.getString(transcriptionIdArg) ?: ""
        }
    }
}