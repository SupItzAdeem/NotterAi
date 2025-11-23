package week11.st968323.finalproject.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st968323.finalproject.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onNavigateToNotes: () -> Unit
) {
    val user = authViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Home")
        Text(text = "Welcome: ${user?.email ?: "Guest"}")

        Button(onClick = onNavigateToNotes) {
            Text("My Notes")
        }

        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}
