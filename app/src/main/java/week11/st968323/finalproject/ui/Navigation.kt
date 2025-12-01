package week11.st968323.finalproject.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import week11.st968323.finalproject.viewmodel.AuthViewModel
import week11.st968323.finalproject.viewmodel.NoteViewModel

object NavRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val NOTES = "notes"
    const val EDITOR = "editor"
    const val PROFILE = "profile"
    const val PICK_IMAGE = "pickImage"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val noteViewModel: NoteViewModel = viewModel()

    LaunchedEffect(Unit) {
        if (authViewModel.currentUser != null) {
            noteViewModel.startObservingNotes()
        }
    }

    val startDestination =
        if (authViewModel.currentUser != null) NavRoutes.NOTES else NavRoutes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    noteViewModel.startObservingNotes()
                    navController.navigate(NavRoutes.NOTES) {
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
                    noteViewModel.startObservingNotes()
                    navController.navigate(NavRoutes.NOTES) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.NOTES) {
            NotesScreen(
                noteViewModel = noteViewModel,
                authViewModel = authViewModel,
                onNavigateToProfile = { navController.navigate(NavRoutes.PROFILE) },
                onAddNew = { navController.navigate(NavRoutes.EDITOR) },
                onEditNote = { id ->
                    navController.navigate("${NavRoutes.EDITOR}?id=$id")
                }
            )
        }

        composable(
            route = "${NavRoutes.EDITOR}?id={id}",
            arguments = listOf(
                navArgument("id") { defaultValue = "" }
            )
        ) { entry ->
            val noteId = entry.arguments?.getString("id") ?: ""
            NoteEditorScreen(
                noteViewModel = noteViewModel,
                noteId = noteId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.NOTES) { inclusive = true }
                    }
                },
                onEditImage = { navController.navigate(NavRoutes.PICK_IMAGE) }
            )
        }

        composable(NavRoutes.PICK_IMAGE) {
            ImagePicker(
                authViewModel = authViewModel,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
