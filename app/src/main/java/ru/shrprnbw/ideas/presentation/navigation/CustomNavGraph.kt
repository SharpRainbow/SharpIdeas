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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.shrprnbw.ideas.presentation.screens.login.LoginScreen
import ru.shrprnbw.ideas.presentation.screens.notes_list.NoteListScreen
import ru.shrprnbw.ideas.presentation.screens.profile.ProfileScreen
import ru.shrprnbw.ideas.presentation.screens.profile_edit.ProfileEditScreen
import ru.shrprnbw.ideas.presentation.screens.register.RegisterScreen
import ru.shrprnbw.ideas.presentation.screens.search.SearchScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            enterTransition = {
                when (targetState.destination.route) {
                    Screen.Search.route -> fadeIn(animationSpec = tween(Screen.Search.ANIMATION_DUR))
                    else -> slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(Screen.Search.ANIMATION_DUR)
                    )
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screen.Search.route -> fadeOut(animationSpec = tween(Screen.Search.ANIMATION_DUR))
                    else -> fadeOut(animationSpec = tween(Screen.Search.ANIMATION_DUR))
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Screen.Search.route -> fadeIn(animationSpec = tween(Screen.Search.ANIMATION_DUR))
                    else -> fadeIn(animationSpec = tween(Screen.Search.ANIMATION_DUR))
                }
            },
            popExitTransition = {
                when (initialState.destination.route) {
                    Screen.Search.route -> fadeOut(animationSpec = tween(Screen.Search.ANIMATION_DUR))
                    else -> slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(Screen.Search.ANIMATION_DUR)
                    )
                }
            }
        ) {
            composable(
                route = Screen.Login.route
            ) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Screen.Notes.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    },
                    onRegisterClicked = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }
            composable(
                route = Screen.UserInfo.route
            ) {
                ProfileScreen(
                    onBackClicked = {
                        navController.popBackStack()
                    },
                    onLogOut = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.UserInfo.route) {
                                inclusive = true
                            }
                        }
                    },
                    onEditProfile = {
                        navController.navigate(Screen.EditUserInfo.route)
                    }
                )
            }
            composable(
                route = Screen.EditUserInfo.route
            ) {
                ProfileEditScreen {
                    navController.popBackStack()
                }
            }
            composable(
                route = Screen.Register.route
            ) {
                RegisterScreen(
                    onBackClicked = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(
                route = Screen.Notes.route
            ) {
                NoteListScreen(
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onSearchTriggered = {
                        navController.navigate(Screen.Search.route)
                    },
                    onProfileClicked = {
                        navController.navigate(Screen.UserInfo.route)
                    }
                )
            }
            composable(
                route = Screen.Search.route
            ) {
                SearchScreen(
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

sealed class Screen(val route: String) {

    data object Search : Screen("search") {
        const val ANIMATION_DUR = 500
        const val ANIMATION_KEY = "search_screen_search_bar"
    }

    data object Register : Screen("register")

    data object Login : Screen("login")

    data object UserInfo : Screen("user_info")

    data object EditUserInfo : Screen("user_info_edit")

    data object Notes : Screen("notes")

    data object CreateNote : Screen("create_note")

    data object EditNote : Screen("edit_note/{note_id}") {

        private const val noteIdArg = "note_id"
        val arguments = listOf(
            navArgument(noteIdArg) { type = NavType.IntType }
        )

        fun createRoute(noteId: Int): String {
            return "edit_note/$noteId"
        }

        fun getNoteId(arguments: Bundle?): Int {
            return arguments?.getInt(noteIdArg) ?: 0
        }
    }
}