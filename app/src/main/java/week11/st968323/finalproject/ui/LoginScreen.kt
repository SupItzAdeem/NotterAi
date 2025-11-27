package week11.st968323.finalproject.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import week11.st968323.finalproject.ui.components.InputField
import week11.st968323.finalproject.ui.components.Lavender
import week11.st968323.finalproject.ui.components.LavenderButton
import week11.st968323.finalproject.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState.data) {
        if (authState.data != null) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(30.dp))

        InputField(email, "Email") { email = it }
        Spacer(Modifier.height(16.dp))
        InputField(password, "Password") { password = it }

        authState.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))

        LavenderButton("Login") {
            authViewModel.login(email, password)
        }

        Spacer(Modifier.height(12.dp))

        Row {
            Text("Don't have an account? ", color = Color.Black)

            Text(
                "Register",
                color = Lavender,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
