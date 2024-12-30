package ie.setu.tazq.firebase.database

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import ie.setu.tazq.data.model.FamilyGroup
import ie.setu.tazq.data.model.Invitation
import ie.setu.tazq.data.model.InvitationStatus
import ie.setu.tazq.data.model.User
import ie.setu.tazq.data.rules.Constants
import ie.setu.tazq.data.rules.Constants.TASK_COLLECTION
import ie.setu.tazq.data.rules.Constants.USER_COLLECTION
import ie.setu.tazq.data.rules.Constants.USER_EMAIL
import ie.setu.tazq.firebase.services.AuthService
import ie.setu.tazq.firebase.services.FirestoreService
import ie.setu.tazq.firebase.services.Task
import ie.setu.tazq.firebase.services.Tasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirestoreRepository
@Inject constructor(
    private val auth: AuthService,
    private val firestore: FirebaseFirestore
) : FirestoreService {

    override suspend fun getAll(email: String): Tasks {
        return firestore.collection(TASK_COLLECTION)
            .whereEqualTo(USER_EMAIL, email)
            .dataObjects()
    }

    override suspend fun get(email: String, taskId: String): Task? {
        return firestore.collection(TASK_COLLECTION)
            .document(taskId).get().await().toObject()
    }

    override suspend fun insert(email: String, task: Task) {
        val taskWithEmail = task.copy(email = email)
        firestore.collection(TASK_COLLECTION)
            .add(taskWithEmail)
            .await()
    }

    override suspend fun update(email: String, task: Task) {
        val taskWithModifiedDate = task.copy(dateModified = Date())
        firestore.collection(TASK_COLLECTION)
            .document(task._id)
            .set(taskWithModifiedDate)
            .await()
    }

    override suspend fun delete(email: String, taskId: String) {
        firestore.collection(TASK_COLLECTION)
            .document(taskId)
            .delete()
            .await()
    }

    override suspend fun createFamilyGroup(group: FamilyGroup) {
        firestore.collection(Constants.FAMILY_GROUP_COLLECTION)
            .document(group.groupId)
            .set(group)
            .await()
    }

    override suspend fun getFamilyGroups(userId: String): Flow<List<FamilyGroup>> {
        return firestore.collection(Constants.FAMILY_GROUP_COLLECTION)
            .whereArrayContains("memberIds", userId)
            .dataObjects()
    }

    override suspend fun updateFamilyGroupName(groupId: String, newName: String) {
        firestore.collection(Constants.FAMILY_GROUP_COLLECTION)
            .document(groupId)
            .update("groupName", newName)
            .await()
    }

    override suspend fun deleteFamilyGroup(groupId: String) {
        firestore.collection(Constants.FAMILY_GROUP_COLLECTION)
            .document(groupId)
            .delete()
            .await()
    }

    override suspend fun removeUserFromFamilyGroup(groupId: String, userId: String) {
        val groupRef = firestore.collection(Constants.FAMILY_GROUP_COLLECTION).document(groupId)
        val groupSnapshot = groupRef.get().await()
        val group = groupSnapshot.toObject(FamilyGroup::class.java)
        if (group != null) {
            val updatedMemberIds = group.memberIds.toMutableList()
            updatedMemberIds.remove(userId)
            groupRef.update("memberIds", updatedMemberIds).await()
        }
    }

    override suspend fun createInvitation(invitation: Invitation) {
        firestore.collection(Constants.INVITATION_COLLECTION)
            .document(invitation.invitationId)
            .set(invitation)
            .await()
    }

    override suspend fun getUserProfile(userId: String): User? {
        return firestore.collection(USER_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject<User>()
    }

    override suspend fun updateUserProfile(user: User) {
        firestore.collection(USER_COLLECTION).document(user.userId).set(user).await()
    }

    override suspend fun getInvitationsByRecipient(email: String): Flow<List<Invitation>> {
        return firestore.collection(Constants.INVITATION_COLLECTION)
            .whereEqualTo("recipientEmail", email)
            .dataObjects()
    }

    override suspend fun updateInvitationStatus(invitationId: String, status: InvitationStatus) {
        firestore.collection(Constants.INVITATION_COLLECTION)
            .document(invitationId)
            .update("status", status)
            .await()
    }

    override suspend fun addUserToFamilyGroup(groupId: String, userId: String) {
        val groupRef = firestore.collection(Constants.FAMILY_GROUP_COLLECTION).document(groupId)
        groupRef.update("memberIds", FieldValue.arrayUnion(userId)).await()
    }

    override suspend fun getTasksByFamilyGroup(groupId: String): Tasks {
        return firestore.collection(TASK_COLLECTION)
            .whereEqualTo("familyGroupId", groupId)
            .dataObjects()
    }

    override suspend fun getTasksAssignedToUser(userId: String, groupId: String?): Tasks {
        val query = if (groupId != null) {
            firestore.collection(TASK_COLLECTION)
                .whereEqualTo("familyGroupId", groupId)
                .whereArrayContains("assignedUserIds", userId)
        } else {
            firestore.collection(TASK_COLLECTION)
                .whereArrayContains("assignedUserIds", userId)
        }
        return query.dataObjects()
    }

    override suspend fun updateTaskAssignments(taskId: String, assignedUserIds: List<String>) {
        firestore.collection(TASK_COLLECTION)
            .document(taskId)
            .update("assignedUserIds", assignedUserIds)
            .await()
    }
}
