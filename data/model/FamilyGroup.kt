package ie.setu.tazq.data.model

import com.google.firebase.firestore.DocumentId
import java.util.UUID

data class FamilyGroup(
    @DocumentId val groupId: String = UUID.randomUUID().toString(), // Auto-generate UUID
    val groupName: String = "",
    val adminId: String = "", // UUID of the admin user
    val memberIds: List<String> = listOf() // List of member UUIDs
)
