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
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState.data) {
        if (authState.data != null) onRegisterSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(30.dp))

        InputField(fullName, "Full Name") { fullName = it }
        Spacer(modifier = Modifier.height(16.dp))
        InputField(email, "Email") { email = it }
        Spacer(modifier = Modifier.height(16.dp))
        InputField(password, "Password") { password = it }

        authState.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(2.dp))

        LavenderButton("Register") {
            authViewModel.register(fullName, email, password)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Text("Already have an account? ", color = Color.Black)

            Text(
                "Login",
                color = Lavender,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }

    }
}
