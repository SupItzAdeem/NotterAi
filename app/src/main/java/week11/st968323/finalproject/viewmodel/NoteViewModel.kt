package week11.st968323.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import week11.st968323.finalproject.data.FirestoreRepository
import week11.st968323.finalproject.model.NoteItem
import week11.st968323.finalproject.util.Resource
import week11.st968323.finalproject.util.UiState

class NoteViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _noteListState = MutableStateFlow(UiState<List<NoteItem>>(isLoading = true))
    val noteListState: StateFlow<UiState<List<NoteItem>>> = _noteListState

    private val _noteActionState = MutableStateFlow(UiState<Unit>())
    val noteActionState: StateFlow<UiState<Unit>> = _noteActionState

    init {
    }

    private fun observeNotes() {
        viewModelScope.launch {
            repo.getUserNotes().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> _noteListState.value = UiState(data = resource.data)
                    is Resource.Error -> _noteListState.value = UiState(error = resource.message)
                    Resource.Loading -> _noteListState.value = UiState(isLoading = true)
                }
            }
        }
    }

    fun addNote(title: String, content: String) {
        if (title.isBlank()) {
            _noteActionState.value = UiState(error = "Title cannot be empty")
            return
        }

        viewModelScope.launch {
            _noteActionState.value = UiState(isLoading = true)
            when (val result = repo.addNote(title, content)) {
                is Resource.Success -> _noteActionState.value = UiState(data = Unit)
                is Resource.Error -> _noteActionState.value = UiState(error = result.message)
                Resource.Loading -> {}
            }
        }
    }

    fun updateNote(id: String, title: String, content: String) {
        viewModelScope.launch {
            _noteActionState.value = UiState(isLoading = true)
            when (val result = repo.updateNote(id, title, content)) {
                is Resource.Success -> _noteActionState.value = UiState(data = Unit)
                is Resource.Error -> _noteActionState.value = UiState(error = result.message)
                Resource.Loading -> {}
            }
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            _noteActionState.value = UiState(isLoading = true)
            when (val result = repo.deleteNote(id)) {
                is Resource.Success -> _noteActionState.value = UiState(data = Unit)
                is Resource.Error -> _noteActionState.value = UiState(error = result.message)
                Resource.Loading -> {}
            }
        }
    }

    fun startObservingNotes() {
        observeNotes()
    }
}
