package week11.st968323.finalproject.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st968323.finalproject.viewmodel.NoteViewModel

@Composable
fun NoteEditorScreen(
    noteViewModel: NoteViewModel,
    noteId: String,
    onBack: () -> Unit
) {
    val noteListState by noteViewModel.noteListState.collectAsState()

    // state fields for editing
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // Load note values only when editing
    LaunchedEffect(noteId, noteListState.data) {
        if (noteId.isNotEmpty()) {
            val note = noteListState.data?.find { it.id == noteId }
            if (note != null) {
                title = note.title
                content = note.content
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = if (noteId.isEmpty()) "Create Note" else "Edit Note",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )

            // STT microphone
            IconButton(
                onClick = {
                    // implemented Speech-to-Text here
                }
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Record audio")
            }

            Button(
                onClick = {
                    if (noteId.isEmpty()) {
                        // Create new note
                        noteViewModel.addNote(title, content)
                    } else {
                        // Update existing note
                        noteViewModel.updateNote(noteId, title, content)
                    }
                    onBack()
                }
            ) {
                Text(if (noteId.isEmpty()) "Save Note" else "Update Note")
            }

            Button(onClick = onBack) {
                Text("Cancel")
            }
        }
    }
}
