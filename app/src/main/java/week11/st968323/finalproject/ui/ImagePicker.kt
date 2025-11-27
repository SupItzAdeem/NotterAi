package week11.st968323.finalproject.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import week11.st968323.finalproject.viewmodel.AuthViewModel

@Composable
fun ImagePicker(
    authViewModel: AuthViewModel,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
            if (bytes != null) {
                authViewModel.updateProfileImage(bytes)
            }
        }
        onDone()
    }

    LaunchedEffect(Unit) {
        launcher.launch("image/*")
    }
}
