package ru.shrprnbw.ideas.presentation.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.shrprnbw.ideas.presentation.screens.login.LoginScreen
import ru.shrprnbw.ideas.presentation.screens.profile.ProfileScreen
import ru.shrprnbw.ideas.presentation.screens.profile_edit.ProfileEditScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen {
                navController.navigate(Screen.UserInfo.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            }
        }
        composable(
            route = Screen.UserInfo.route
        ) {
            ProfileScreen(
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
    }
}

sealed class Screen(val route: String) {

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