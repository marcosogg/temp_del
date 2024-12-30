package ie.setu.tazq.data.model

import androidx.room.Entity
import com.google.firebase.firestore.DocumentId
import java.util.Date

@Entity
data class TaskModel(
    @DocumentId val _id: String = "N/A",
    val title: String = "Untitled",
    val priority: String = "MEDIUM",  // Store as string for Firestore compatibility
    val description: String = "No description",
    val category: String = "Personal",
    val dateCreated: Date = Date(),
    val dateModified: Date = Date(),
    val isDone: Boolean = false,
    var email: String = "joe@bloggs.com",  // Required for Firebase
    val familyGroupId: String? = null, // Add this line for Family Group ID
    val assignedUserIds: List<String> = emptyList() // Add this line for assigned user IDs
)

// For testing/preview purposes
val fakeTasks = List(30) { i ->
    TaskModel(
        _id = "12345$i",
        title = "Task $i",
        description = "Description $i",
        dateCreated = Date(),
        dateModified = Date()
    )
}
