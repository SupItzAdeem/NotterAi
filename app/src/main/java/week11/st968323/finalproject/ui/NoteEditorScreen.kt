package week11.st968323.finalproject.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import week11.st968323.finalproject.ui.components.LavenderButton
import week11.st968323.finalproject.ui.components.LavenderTopBar
import week11.st968323.finalproject.util.SpeechToTextHelper
import week11.st968323.finalproject.util.TextToSpeechHelper
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
    var isListening by remember { mutableStateOf(false) }
    var sttError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val ttsHelper = remember { TextToSpeechHelper(context) }

    val sttHelper = remember {
        SpeechToTextHelper(
            context = context,
            onResult = { recognized ->
                content = if (content.isBlank()) recognized else content + "\n" + recognized
            },
            onError = { err ->
                sttError = err
            },
            onListeningChanged = { listening ->
                isListening = listening
            }
        )
    }

    // mic permission
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) sttHelper.startListening()
        else sttError = "Microphone permission denied."
    }

    // load existing note
    LaunchedEffect(noteId, notesState.data) {
        if (noteId.isNotEmpty()) {
            notesState.data?.find { it.id == noteId }?.let { note ->
                title = note.title
                content = note.content
            }
        }
    }

    // cleanup
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
            sttHelper.destroy()
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

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

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

            sttError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Mic
                IconButton(
                    onClick = {
                        val granted =
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                        if (granted) sttHelper.startListening()
                        else micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Record",
                        tint = if (isListening) Color(0xFF7A44A1) else Color(0xFF7A44A1)
                    )
                }

                // Stop Listening (Only when active)
                if (isListening) {
                    IconButton(
                        onClick = { sttHelper.stopListening() }
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop Listening",
                            tint = Color.Red
                        )
                    }
                }

                // Speaker (TTS)
                IconButton(
                    onClick = {
                        scope.launch {
                            val combined = listOf(title, content)
                                .filter { it.isNotBlank() }
                                .joinToString(". ")
                            ttsHelper.speak(combined)
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Read aloud",
                        tint = Color(0xFF7A44A1)
                    )
                }
            }

            LavenderButton(
                text = if (noteId.isEmpty()) "Save Note" else "Update Note"
            ) {
                if (noteId.isEmpty()) noteViewModel.addNote(title, content)
                else noteViewModel.updateNote(noteId, title, content)
                onBack()
            }
        }
    }
}