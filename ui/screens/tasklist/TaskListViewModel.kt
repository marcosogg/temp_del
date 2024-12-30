// ui/screens/tasklist/TaskListViewModel.kt
package ie.setu.tazq.ui.screens.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.setu.tazq.data.Task
import ie.setu.tazq.data.repository.TaskRepository
import ie.setu.tazq.firebase.services.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import ie.setu.tazq.firebase.services.FirestoreService
import ie.setu.tazq.data.model.TaskModel

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val authService: AuthService,
    private val firestoreService: FirestoreService
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _taskModels = MutableStateFlow<List<TaskModel>>(emptyList())
    val taskModels: StateFlow<List<TaskModel>> = _taskModels.asStateFlow()

    private val currentUserId: String = authService.currentUserId

    init {
        viewModelScope.launch {
            repository.getAll(userId = currentUserId).collect { listOfTasks ->
                _tasks.value = listOfTasks
            }
        }
        viewModelScope.launch {
            firestoreService.getAll(authService.email!!).collect { listOfTasks ->
                _taskModels.value = listOfTasks
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun updateTaskStatus(task: Task) {
        viewModelScope.launch {
            repository.updateTaskStatus(task.id, !task.isDone, currentUserId) // Pass currentUserId
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            repository.getAll(currentUserId).collect { allTasks ->  // Filter tasks by currentUserId
                _tasks.value = allTasks.filter { task ->
                    task.title.contains(query, ignoreCase = true) ||
                            task.description.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun sortTasks(sortBy: SortOption) {
        viewModelScope.launch {
            val currentTasks = _tasks.value
            _tasks.value = when (sortBy) {
                SortOption.DATE -> currentTasks.sortedByDescending { it.dateCreated }
                SortOption.PRIORITY -> currentTasks.sortedBy { it.priority.ordinal } // Sort by priority ordinal
                SortOption.CATEGORY -> currentTasks.sortedBy { it.category }
            }
        }
    }
}

enum class SortOption {
    DATE,
    PRIORITY,
    CATEGORY
}

