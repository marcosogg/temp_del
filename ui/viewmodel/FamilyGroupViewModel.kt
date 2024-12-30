package ie.setu.tazq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.setu.tazq.data.model.FamilyGroup
import ie.setu.tazq.data.model.Invitation
import ie.setu.tazq.data.model.InvitationStatus
import ie.setu.tazq.data.model.User
import ie.setu.tazq.firebase.services.AuthService
import ie.setu.tazq.firebase.services.FirestoreService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyGroupViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val authService: AuthService
) : ViewModel() {

    val currentUser: FirebaseUser? get() = authService.currentUser

    private val _familyGroups = MutableStateFlow<List<FamilyGroup>>(emptyList())
    val familyGroups: StateFlow<List<FamilyGroup>> = _familyGroups.asStateFlow()

    init {
        fetchFamilyGroups()
    }

    fun createFamilyGroup(groupName: String) {
        viewModelScope.launch {
            val group = FamilyGroup(
                groupName = groupName,
                adminId = currentUser?.uid ?: "",
                memberIds = listOf(currentUser?.uid ?: "")
            )
            firestoreService.createFamilyGroup(group)
        }
    }

    fun fetchFamilyGroups() {
        val userId = currentUser?.uid ?: return
        viewModelScope.launch {
            firestoreService.getFamilyGroups(userId).collect { groups ->
                _familyGroups.value = groups
            }
        }
    }

    fun updateFamilyGroupName(groupId: String, newName: String) {
        viewModelScope.launch {
            firestoreService.updateFamilyGroupName(groupId, newName)
            fetchFamilyGroups() // Refresh the list after updating
        }
    }

    fun deleteFamilyGroup(groupId: String) {
        viewModelScope.launch {
            firestoreService.deleteFamilyGroup(groupId)
            fetchFamilyGroups() // Refresh the list after deletion
        }
    }

    fun leaveFamilyGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            firestoreService.removeUserFromFamilyGroup(groupId, userId)
            fetchFamilyGroups() // Refresh the list after leaving
        }
    }

    fun inviteUserToGroup(groupId: String, email: String) {
        viewModelScope.launch {
            val invitation = Invitation(
                groupId = groupId,
                senderId = currentUser?.uid ?: "",
                recipientEmail = email,
                status = InvitationStatus.PENDING
            )
            firestoreService.createInvitation(invitation)
        }
    }

    suspend fun getMemberDetails(groupId: String): List<User> {
        // First, fetch the FamilyGroup to get the memberIds
        val familyGroup = _familyGroups.value.find { it.groupId == groupId } ?: return emptyList()
        val memberIds = familyGroup.memberIds

        // Then, fetch user details for each memberId
        return memberIds.map { userId ->
            viewModelScope.async {
                firestoreService.getUserProfile(userId)
            }
        }.awaitAll().filterNotNull()
    }
}
