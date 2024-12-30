package ie.setu.tazq.firebase.services

import ie.setu.tazq.data.model.FamilyGroup
import ie.setu.tazq.data.model.Invitation
import ie.setu.tazq.data.model.InvitationStatus
import ie.setu.tazq.data.model.TaskModel
import ie.setu.tazq.data.model.User
import kotlinx.coroutines.flow.Flow

typealias Task = TaskModel
typealias Tasks = Flow<List<Task>>

interface FirestoreService {
    suspend fun getAll(email: String): Tasks
    suspend fun get(email: String, taskId: String): Task?
    suspend fun insert(email: String, task: Task)
    suspend fun update(email: String, task: Task)
    suspend fun delete(email: String, taskId: String)
    suspend fun createFamilyGroup(group: FamilyGroup)
    suspend fun getFamilyGroups(userId: String): Flow<List<FamilyGroup>>
    suspend fun updateFamilyGroupName(groupId: String, newName: String)
    suspend fun deleteFamilyGroup(groupId: String)
    suspend fun removeUserFromFamilyGroup(groupId: String, userId: String)
    suspend fun getUserProfile(userId: String): User?
    suspend fun createInvitation(invitation: Invitation)

    // Add these missing method signatures
    suspend fun getInvitationsByRecipient(email: String): Flow<List<Invitation>>
    suspend fun updateInvitationStatus(invitationId: String, status: InvitationStatus)
    suspend fun addUserToFamilyGroup(groupId: String, userId: String)
    suspend fun updateUserProfile(user: User) // Add this method to update user profile

}
