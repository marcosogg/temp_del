package ie.setu.tazq.data.model

import java.util.UUID

data class Invitation(
    val invitationId: String = UUID.randomUUID().toString(),
    val groupId: String = "",
    val senderId: String = "", // ID of the user who sent the invitation
    val recipientEmail: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING
)

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}
