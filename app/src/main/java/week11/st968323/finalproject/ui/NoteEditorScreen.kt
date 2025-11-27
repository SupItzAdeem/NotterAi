package week11.st968323.finalproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import week11.st968323.finalproject.ui.components.LavenderButton
import week11.st968323.finalproject.ui.components.LavenderTopBar
import week11.st968323.finalproject.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteViewModel: NoteViewModel,
    noteId: String,
    onBack: () -> Unit
) {
    val notesState by noteViewModel.noteListState.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // Load existing note for editing
    LaunchedEffect(noteId, notesState.data) {
        if (noteId.isNotEmpty()) {
            val note = notesState.data?.find { it.id == noteId }
            if (note != null) {
                title = note.title
                content = note.content
            }
        }
    }

    Scaffold(
        topBar = {
            LavenderTopBar(
                title = "Notter AI",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Content Field
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Write your note here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                maxLines = Int.MAX_VALUE,
                shape = RoundedCornerShape(12.dp)
            )

            // Mic + Speaker Buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { /* TODO STT */ }) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Record",
                            tint = Color(0xFF7A44A1) // purple like Figma
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { /* TODO TTS */ }) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Read aloud",
                            tint = Color(0xFF7A44A1)
                        )
                    }
                }
            }

            // Save / Update Button
            LavenderButton(
                text = if (noteId.isEmpty()) "Save Note" else "Update Note"
            ) {
                if (noteId.isEmpty()) {
                    noteViewModel.addNote(title, content)
                } else {
                    noteViewModel.updateNote(noteId, title, content)
                }
                onBack()
            }
        }
    }
}
