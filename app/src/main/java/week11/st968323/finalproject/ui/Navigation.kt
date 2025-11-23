package week11.st968323.finalproject.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import week11.st968323.finalproject.viewmodel.AuthViewModel
import week11.st968323.finalproject.viewmodel.NoteViewModel

object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val NOTES = "notes"
    const val EDITOR = "editor"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel()

    val startDestination = if (authViewModel.currentUser != null) {
        NavRoutes.HOME
    } else {
        NavRoutes.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.REGISTER)
                }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                },
                onNavigateToNotes = {
                    navController.navigate(NavRoutes.NOTES)
                }
            )
        }

        composable(NavRoutes.NOTES) {
            val noteViewModel: NoteViewModel = viewModel()
            NotesScreen(
                noteViewModel = noteViewModel,
                onAddNew = { navController.navigate(NavRoutes.EDITOR) },
                onEditNote = { noteId ->
                    navController.navigate("${NavRoutes.EDITOR}?id=$noteId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("${NavRoutes.EDITOR}?id={id}") { backStackEntry ->
            val noteViewModel: NoteViewModel = viewModel()
            val noteId = backStackEntry.arguments?.getString("id") ?: ""

            NoteEditorScreen(
                noteViewModel = noteViewModel,
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
