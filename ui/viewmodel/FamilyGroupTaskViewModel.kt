package ie.setu.tazq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.setu.tazq.data.model.TaskModel
import ie.setu.tazq.firebase.services.AuthService
import ie.setu.tazq.firebase.services.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyGroupTaskViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val authService: AuthService
) : ViewModel() {

    private val _tasks: MutableStateFlow<List<TaskModel>> = MutableStateFlow(emptyList())
    val tasks: StateFlow<List<TaskModel>> = _tasks.asStateFlow()

    fun fetchTasksForFamilyGroup(groupId: String) {
        viewModelScope.launch {
            firestoreService.getTasksByFamilyGroup(groupId).collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun fetchTasksAssignedToUser(userId: String, groupId: String?) {
        viewModelScope.launch {
            firestoreService.getTasksAssignedToUser(userId, groupId).collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun assignTaskToUsers(taskId: String, userIds: List<String>) {
        viewModelScope.launch {
            firestoreService.updateTaskAssignments(taskId, userIds)
            // TODO: Consider refreshing the task list after updating
        }
    }
}
