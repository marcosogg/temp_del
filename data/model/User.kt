package ie.setu.tazq.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val userId: String = "",
    val displayName: String = "",
    val email: String = ""
    // Add other fields as needed (e.g., profile picture URL, etc.)
)
