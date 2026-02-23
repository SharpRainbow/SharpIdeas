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
import ru.shrprnbw.ideas.presentation.screens.board.BoardScreen
import ru.shrprnbw.ideas.presentation.screens.group_management.GroupManagementScreen
import ru.shrprnbw.ideas.presentation.screens.group_notes.GroupNotesScreen
import ru.shrprnbw.ideas.presentation.screens.keywords.KeywordsScreen
import ru.shrprnbw.ideas.presentation.screens.login.LoginScreen
import ru.shrprnbw.ideas.presentation.screens.main.MainScreenWithDrawer
import ru.shrprnbw.ideas.presentation.screens.my_tasks.MyTasksScreen
import ru.shrprnbw.ideas.presentation.screens.note_editor.NoteEditorScreen
import ru.shrprnbw.ideas.presentation.screens.notes_list.NoteListScreen
import ru.shrprnbw.ideas.presentation.screens.profile.ProfileScreen
import ru.shrprnbw.ideas.presentation.screens.profile_edit.ProfileEditScreen
import ru.shrprnbw.ideas.presentation.screens.register.RegisterScreen
import ru.shrprnbw.ideas.presentation.screens.search.SearchScreen
import ru.shrprnbw.ideas.presentation.screens.shared_notes.SharedNotesScreen
import ru.shrprnbw.ideas.presentation.screens.summaries.SummariesScreen
import ru.shrprnbw.ideas.presentation.screens.summary_detail.SummaryDetailScreen
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
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(ANIMATION_DUR)
                    )
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Search.route -> fadeOut(animationSpec = tween(ANIMATION_DUR))
                    else -> slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(ANIMATION_DUR)
                    )
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Screen.Search.route -> fadeIn(animationSpec = tween(ANIMATION_DUR))
                    else -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(ANIMATION_DUR)
                    )
                }
            },
            popExitTransition = {
                when (initialState.destination.route) {
                    Screen.Search.route -> fadeOut(animationSpec = tween(ANIMATION_DUR))
                    else -> slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
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
                        navController = navController,
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
                            },
                            onBoardClicked = { noteId ->
                                navController.navigate(Screen.Board.createRoute(noteId))
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
                        },
                        onNoteClicked = { noteId ->
                            navController.navigate(Screen.EditNote.createRoute(noteId))
                        },
                        onBoardClicked = { noteId ->
                            navController.navigate(Screen.Board.createRoute(noteId))
                        }
                    )
                }

                composable(route = Screen.Tags.route) {
                    MainScreenWithDrawer(
                        selectedScreen = NavigationScreen.TagManagement,
                        currentRoute = Screen.Tags,
                        navController = navController,
                        gesturesEnabled = true
                    ) { drawerState, scope ->
                        TagManagementScreen(
                            onMenuClicked = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }
                }

                composable(route = Screen.Groups.route) {
                    MainScreenWithDrawer(
                        selectedScreen = NavigationScreen.GroupManagement,
                        currentRoute = Screen.Groups,
                        navController = navController,
                        gesturesEnabled = true
                    ) { drawerState, scope ->
                        GroupManagementScreen(
                            onMenuClicked = {
                                scope.launch { drawerState.open() }
                            },
                            onGroupClicked = { group ->
                                navController.navigate(
                                    Screen.GroupNotes.createRoute(
                                        group.id,
                                        group.name
                                    )
                                )
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
                        onTranscriptionsClicked = { hasAccess ->
                            navController.navigate(
                                Screen.Transcriptions.createRoute(
                                    noteId,
                                    hasAccess = hasAccess
                                )
                            )
                        },
                        onKeywordsClicked = { hasAccess ->
                            navController.navigate(
                                Screen.Keywords.createRoute(
                                    noteId,
                                    hasAccess = hasAccess
                                )
                            )
                        },
                        onSummariesClicked = { hasAccess ->
                            navController.navigate(
                                Screen.Summaries.createRoute(
                                    noteId,
                                    hasAccess = hasAccess
                                )
                            )
                        }
                    )
                }

                composable(
                    route = Screen.Board.route,
                    arguments = Screen.Board.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.Board.getNoteId(navBackStackEntry.arguments)
                    BoardScreen(
                        noteId = noteId,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onNavigateToNote = { refNoteId, noteType ->
                            if (noteType == "BOARD") {
                                navController.navigate(Screen.Board.createRoute(refNoteId))
                            } else {
                                navController.navigate(Screen.EditNote.createRoute(refNoteId))
                            }
                        }
                    )
                }

                composable(
                    route = Screen.Transcriptions.route,
                    arguments = Screen.Transcriptions.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.Transcriptions.getNoteId(navBackStackEntry.arguments)
                    val hasAccess = Screen.Transcriptions.getHasAccess(navBackStackEntry.arguments)
                    TranscriptionsScreen(
                        noteId = noteId,
                        hasAccess = hasAccess,
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

                composable(
                    route = Screen.Keywords.route,
                    arguments = Screen.Keywords.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.Keywords.getNoteId(navBackStackEntry.arguments)
                    val hasAccess = Screen.Keywords.getHasAccess(navBackStackEntry.arguments)
                    KeywordsScreen(
                        noteId = noteId,
                        hasAccess = hasAccess,
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.Summaries.route,
                    arguments = Screen.Summaries.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.Summaries.getNoteId(navBackStackEntry.arguments)
                    val hasAccess = Screen.Summaries.getHasAccess(navBackStackEntry.arguments)
                    SummariesScreen(
                        noteId = noteId,
                        hasAccess = hasAccess,
                        onBackClicked = {
                            navController.popBackStack()
                        },
                        onSummaryClicked = { summaryId ->
                            navController.navigate(
                                Screen.SummaryDetail.createRoute(noteId, summaryId)
                            )
                        }
                    )
                }

                composable(
                    route = Screen.SummaryDetail.route,
                    arguments = Screen.SummaryDetail.arguments
                ) { navBackStackEntry ->
                    val noteId = Screen.SummaryDetail.getNoteId(navBackStackEntry.arguments)
                    val summaryId = Screen.SummaryDetail.getSummaryId(navBackStackEntry.arguments)
                    SummaryDetailScreen(
                        noteId = noteId,
                        summaryId = summaryId,
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.SharedNotes.route
                ) {
                    MainScreenWithDrawer(
                        selectedScreen = NavigationScreen.SharedNotes,
                        currentRoute = Screen.SharedNotes,
                        navController = navController,
                        gesturesEnabled = true
                    ) { drawerState, scope ->
                        SharedNotesScreen(
                            onMenuClicked = {
                                scope.launch { drawerState.open() }
                            },
                            onNoteClicked = { noteId ->
                                navController.navigate(Screen.EditNote.createRoute(noteId))
                            },
                            onBoardClicked = { noteId ->
                                navController.navigate(Screen.Board.createRoute(noteId))
                            }
                        )
                    }
                }

                composable(
                    route = Screen.SharedNotes.route
                ) {
                    MainScreenWithDrawer(
                        selectedScreen = NavigationScreen.SharedNotes,
                        currentRoute = Screen.SharedNotes,
                        navController = navController,
                        gesturesEnabled = true
                    ) { drawerState, scope ->
                        SharedNotesScreen(
                            onMenuClicked = {
                                scope.launch { drawerState.open() }
                            },
                            onNoteClicked = { noteId ->
                                navController.navigate(Screen.EditNote.createRoute(noteId))
                            },
                            onBoardClicked = { noteId ->
                                navController.navigate(Screen.Board.createRoute(noteId))
                            }
                        )
                    }
                }

                composable(
                    route = Screen.MyTasks.route
                ) {
                    MainScreenWithDrawer(
                        selectedScreen = NavigationScreen.MyTasks,
                        currentRoute = Screen.MyTasks,
                        navController = navController,
                        gesturesEnabled = true
                    ) { drawerState, scope ->
                        MyTasksScreen(
                            onMenuClicked = {
                                scope.launch { drawerState.open() }
                            },
                            onBoardClicked = { noteId ->
                                navController.navigate(Screen.Board.createRoute(noteId))
                            }
                        )
                    }
                }

                composable(
                    route = Screen.GroupNotes.route,
                    arguments = Screen.GroupNotes.arguments
                ) {
                    val groupId = Screen.GroupNotes.getGroupId(it.arguments)
                    val groupName = Screen.GroupNotes.getGroupName(it.arguments)
                    GroupNotesScreen(
                        groupId = groupId,
                        screenTitle = groupName,
                        onNoteClicked = { noteId ->
                            navController.navigate(Screen.EditNote.createRoute(noteId))
                        },
                        onBoardClicked = { noteId ->
                            navController.navigate(Screen.Board.createRoute(noteId))
                        },
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
    data object SharedNotes : Screen("shared_notes")
    data object MyTasks : Screen("my_tasks")
    data object Search : Screen("search")
    data object Tags : Screen("tags")
    data object Groups : Screen("groups")
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

    data object Board : Screen("board/{note_id}") {
        private const val noteIdArg = "note_id"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType }
        )

        fun createRoute(noteId: String): String {
            return "board/$noteId"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }
    }

    data object Transcriptions : Screen("transcriptions/{note_id}?has_access={has_access}") {
        private const val noteIdArg = "note_id"
        private const val accessArg = "has_access"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType },
            navArgument(accessArg) {
                type = NavType.BoolType
                defaultValue = false
            }
        )

        fun createRoute(noteId: String, hasAccess: Boolean = false): String {
            return "transcriptions/$noteId?has_access=$hasAccess"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }

        fun getHasAccess(arguments: Bundle?): Boolean {
            return arguments?.getBoolean(accessArg) ?: false
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

    data object Keywords : Screen("keywords/{note_id}?has_access={has_access}") {
        private const val noteIdArg = "note_id"
        private const val accessArg = "has_access"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType },
            navArgument(accessArg) {
                type = NavType.BoolType
                defaultValue = false
            }
        )

        fun createRoute(noteId: String, hasAccess: Boolean = false): String {
            return "keywords/$noteId?has_access=$hasAccess"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }

        fun getHasAccess(arguments: Bundle?): Boolean {
            return arguments?.getBoolean(accessArg) ?: false
        }
    }

    data object Summaries : Screen("summaries/{note_id}?has_access={has_access}") {
        private const val noteIdArg = "note_id"
        private const val accessArg = "has_access"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType },
            navArgument(accessArg) {
                type = NavType.BoolType
                defaultValue = false
            }
        )

        fun createRoute(noteId: String, hasAccess: Boolean = false): String {
            return "summaries/$noteId?has_access=$hasAccess"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }

        fun getHasAccess(arguments: Bundle?): Boolean {
            return arguments?.getBoolean(accessArg) ?: false
        }
    }

    data object SummaryDetail : Screen("summary/{note_id}/{summary_id}") {
        private const val noteIdArg = "note_id"
        private const val summaryIdArg = "summary_id"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.StringType },
            navArgument(summaryIdArg) { type = NavType.StringType }
        )

        fun createRoute(noteId: String, summaryId: String): String {
            return "summary/$noteId/$summaryId"
        }

        fun getNoteId(arguments: Bundle?): String {
            return arguments?.getString(noteIdArg) ?: ""
        }

        fun getSummaryId(arguments: Bundle?): String {
            return arguments?.getString(summaryIdArg) ?: ""
        }
    }

    data object GroupNotes : Screen("notes/{group_id}?group_name={group_name}") {
        private const val groupIdArg = "group_id"
        private const val groupNameArg = "group_name"
        val arguments = listOf(
            navArgument(groupIdArg) { type = NavType.LongType },
            navArgument(groupNameArg) {
                type = NavType.StringType
                defaultValue = ""
            }
        )

        fun createRoute(groupId: Long, groupName: String): String {
            return "notes/$groupId?group_name=$groupName"
        }

        fun getGroupId(arguments: Bundle?): Long {
            return arguments?.getLong(groupIdArg) ?: -1L
        }

        fun getGroupName(arguments: Bundle?): String {
            return arguments?.getString(groupNameArg) ?: ""
        }

    }
}