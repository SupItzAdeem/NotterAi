package week11.st968323.finalproject.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st968323.finalproject.model.NoteItem
import week11.st968323.finalproject.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    noteViewModel: NoteViewModel,
    onAddNew: () -> Unit,
    onEditNote: (String) -> Unit,
    onBack: () -> Unit
) {
    val listState by noteViewModel.noteListState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNew) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("My Notes", style = MaterialTheme.typography.titleLarge)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listState.data.orEmpty()) { note ->
                    NoteRow(
                        note = note,
                        onReadAloud = {
                            // TTS (Text to Speech) implementation here
                        },
                        onEdit = { onEditNote(note.id) },
                        onDelete = {
                            noteViewModel.deleteNote(note.id)
                        }
                    )
                }
            }

            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}

@Composable
fun NoteRow(
    note: NoteItem,
    onReadAloud: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(note.title, style = MaterialTheme.typography.titleMedium)
                Text(note.content, style = MaterialTheme.typography.bodyMedium)
            }

            Row {
                IconButton(onClick = onReadAloud) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Read aloud")
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
