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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredNotes = MutableStateFlow<List<NoteItem>>(emptyList())
    val filteredNotes: StateFlow<List<NoteItem>> = _filteredNotes

    private val _noteActionState = MutableStateFlow(UiState<Unit>())
    val noteActionState: StateFlow<UiState<Unit>> = _noteActionState

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            repo.getUserNotes().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val notes = resource.data
                        _noteListState.value = UiState(data = notes)

                        if (_searchQuery.value.isBlank()) {
                            _filteredNotes.value = notes
                        } else {
                            applySearch(notes)
                        }
                    }

                    is Resource.Error ->
                        _noteListState.value = UiState(error = resource.message)

                    Resource.Loading ->
                        _noteListState.value = UiState(isLoading = true)
                }
            }
        }
    }


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applySearch(_noteListState.value.data ?: emptyList())
    }

    private fun applySearch(allNotes: List<NoteItem>) {
        val q = _searchQuery.value.lowercase()

        _filteredNotes.value =
            if (q.isBlank()) allNotes
            else allNotes.filter { note ->
                note.title.lowercase().contains(q) ||
                        note.content.lowercase().contains(q)
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

    fun resetSearch() {
        _searchQuery.value = ""
        _filteredNotes.value = _noteListState.value.data ?: emptyList()
    }

}
