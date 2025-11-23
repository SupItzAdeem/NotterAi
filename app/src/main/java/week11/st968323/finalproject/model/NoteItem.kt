package week11.st968323.finalproject.model

data class NoteItem(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)