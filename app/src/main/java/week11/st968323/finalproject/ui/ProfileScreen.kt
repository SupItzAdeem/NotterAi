package week11.st968323.finalproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import week11.st968323.finalproject.ui.components.Lavender
import week11.st968323.finalproject.ui.components.LavenderButton
import week11.st968323.finalproject.ui.components.LavenderTopBar
import week11.st968323.finalproject.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onEditImage: () -> Unit = {}
) {
    val userName by authViewModel.userName.collectAsState()
    val profileUrl by authViewModel.profileImageUrl.collectAsState()

    Scaffold(
        topBar = {
            LavenderTopBar(
                title = "Notter AI",
                onBack = onBack
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(40.dp))

            Box(contentAlignment = Alignment.BottomEnd) {

                if (profileUrl.isNotEmpty()) {
                    AsyncImage(
                        model = profileUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(65.dp))
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(130.dp),
                        tint = Color(0xFF7A44A1)
                    )
                }

                IconButton(
                    onClick = onEditImage,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Lavender
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = userName.ifBlank { "Full Name" },
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.weight(1f))

            LavenderButton(
                text = "Logout",
                modifier = Modifier
                    .padding(24.dp)
            ) {
                onLogout()
            }
        }
    }
}
