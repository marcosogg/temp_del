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
    suspend fun getInvitationsByRecipient(email: String): Flow<List<Invitation>>
    suspend fun updateInvitationStatus(invitationId: String, status: InvitationStatus)
    suspend fun addUserToFamilyGroup(groupId: String, userId: String)
    suspend fun updateUserProfile(user: User)
    /**
     * Retrieves a flow of tasks associated with a specific family group.
     * @param groupId The ID of the family group.
     * @return A flow emitting a list of tasks for the specified group.
     */
    suspend fun getTasksByFamilyGroup(groupId: String): Tasks

    /**
     * Retrieves a flow of tasks assigned to a user, optionally within a specific group.
     * @param userId The ID of the user.
     * @param groupId (Optional) The ID of the family group to filter by. If null, all assigned tasks for the user are retrieved
     * @return A flow emitting a list of tasks assigned to the user
     */
    suspend fun getTasksAssignedToUser(userId: String, groupId: String?): Tasks
    /**
     * Updates the list of user IDs assigned to a specific task.
     * @param taskId The ID of the task to update.
     * @param assignedUserIds List of user ids of the users that a task has been assigned to.
     */
    suspend fun updateTaskAssignments(taskId: String, assignedUserIds: List<String>)
}
