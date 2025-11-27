package week11.st968323.finalproject.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st968323.finalproject.model.NoteItem
import week11.st968323.finalproject.ui.components.LavenderTopBar
import week11.st968323.finalproject.viewmodel.AuthViewModel
import week11.st968323.finalproject.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel,
    onNavigateToProfile: () -> Unit,
    onAddNew: () -> Unit,
    onEditNote: (String) -> Unit
) {
    val listState by noteViewModel.noteListState.collectAsState()
    val userName by authViewModel.userName.collectAsState()

    Scaffold(
        topBar = {
            LavenderTopBar(
                title = "Notter AI",
                onProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNew) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Hello $userName", style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(listState.data.orEmpty()) { note ->
                    NoteCard(note) { onEditNote(note.id) }
                }
            }
        }
    }
}

@Composable
fun NoteCard(note: NoteItem, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(note.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
