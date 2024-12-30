package ie.setu.tazq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.setu.tazq.data.model.Invitation
import ie.setu.tazq.data.model.InvitationStatus
import ie.setu.tazq.firebase.services.AuthService
import ie.setu.tazq.firebase.services.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val authService: AuthService
) : ViewModel() {
    private val _invitations = MutableStateFlow<List<Invitation>>(emptyList())
    val invitations = _invitations.asStateFlow()

    fun fetchInvitations() {
        val currentUserEmail = authService.currentUser?.email ?: return

        viewModelScope.launch {
            firestoreService.getInvitationsByRecipient(currentUserEmail).collect { invitations ->
                _invitations.value = invitations.filter { it.status == InvitationStatus.PENDING }
            }
        }
    }

    fun acceptInvitation(invitationId: String, groupId: String) {
        val currentUserId = authService.currentUserId ?: return

        viewModelScope.launch {
            firestoreService.updateInvitationStatus(invitationId, InvitationStatus.ACCEPTED)
            firestoreService.addUserToFamilyGroup(groupId, currentUserId)
            fetchInvitations() // Refresh invitations
        }
    }

    fun declineInvitation(invitationId: String) {
        viewModelScope.launch {
            firestoreService.updateInvitationStatus(invitationId, InvitationStatus.DECLINED)
            fetchInvitations() // Refresh invitations
        }
    }
}
