package ie.setu.tazq.ui.screens.task

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.setu.tazq.data.Task
import ie.setu.tazq.data.model.TaskModel
import ie.setu.tazq.data.repository.TaskRepository
import ie.setu.tazq.firebase.services.AuthService
import ie.setu.tazq.firebase.services.FirestoreService
import ie.setu.tazq.ui.components.task.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val authService: AuthService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    val currentUserId: String = authService.currentUserId

    private val _taskTitle = MutableStateFlow("")
    val taskTitle: StateFlow<String> = _taskTitle.asStateFlow()

    private val _taskDescription = MutableStateFlow("")
    val taskDescription: StateFlow<String> = _taskDescription.asStateFlow()

    private val _taskPriority = MutableStateFlow(TaskPriority.MEDIUM)
    val taskPriority: StateFlow<TaskPriority> = _taskPriority.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Personal")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isTitleValid = MutableStateFlow(false)
    val isTitleValid: StateFlow<Boolean> = _isTitleValid.asStateFlow()

    private val _isDescriptionValid = MutableStateFlow(false)
    val isDescriptionValid: StateFlow<Boolean> = _isDescriptionValid.asStateFlow()

    private val _showConfirmation = MutableStateFlow(false)
    val showConfirmation: StateFlow<Boolean> = _showConfirmation.asStateFlow()

    private val _titleTouched = mutableStateOf(false)
    val titleTouched: State<Boolean> = _titleTouched

    private val _descriptionTouched = mutableStateOf(false)
    val descriptionTouched: State<Boolean> = _descriptionTouched

    private val _selectedFamilyGroupId = MutableStateFlow<String?>(null)
    val selectedFamilyGroupId: StateFlow<String?> = _selectedFamilyGroupId.asStateFlow()

    private val _assignedUserIds = MutableStateFlow<List<String>>(emptyList())
    val assignedUserIds: StateFlow<List<String>> = _assignedUserIds.asStateFlow()

    fun updateTaskTitle(title: String) {
        _taskTitle.value = title
        _titleTouched.value = true
        validateTitle(title)
    }

    fun updateTaskDescription(description: String) {
        _taskDescription.value = description
        _descriptionTouched.value = true
        validateDescription(description)
    }

    fun updateTaskPriority(priority: TaskPriority) {
        _taskPriority.value = priority
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    private fun validateTitle(title: String) {
        _isTitleValid.value = title.isNotEmpty() && title.length >= 3
    }

    private fun validateDescription(description: String) {
        _isDescriptionValid.value = description.length <= 500
    }

    fun isFormValid(): Boolean {
        return _isTitleValid.value && _isDescriptionValid.value
    }

    fun showConfirmation() {
        _showConfirmation.value = true
    }

    fun hideConfirmation() {
        _showConfirmation.value = false
    }

    fun updateSelectedFamilyGroupId(groupId: String?) {
        _selectedFamilyGroupId.value = groupId
    }

    fun updateAssignedUserIds(userIds: List<String>) {
        _assignedUserIds.value = userIds
    }

    fun addTask() {
        if (!isFormValid()) return

        val newTask = Task(
            userId = currentUserId,
            title = _taskTitle.value,
            priority = _taskPriority.value,
            description = _taskDescription.value,
            category = _selectedCategory.value,
            familyGroupId = _selectedFamilyGroupId.value,
            assignedUserIds = _assignedUserIds.value
        )

        val newTaskModel = TaskModel(
            title = _taskTitle.value,
            priority = _taskPriority.value.name,
            description = _taskDescription.value,
            category = _selectedCategory.value,
            familyGroupId = _selectedFamilyGroupId.value,
            assignedUserIds = _assignedUserIds.value,
            email = authService.email!!,
        )

        viewModelScope.launch {
            repository.insert(newTask) // Insert into local Room database
            firestoreService.insert(authService.email!!, newTaskModel) // Insert into Firestore
            showConfirmation()
            resetForm()
        }
    }

    private fun resetForm() {
        _taskTitle.value = ""
        _taskDescription.value = ""
        _taskPriority.value = TaskPriority.MEDIUM
        _selectedCategory.value = "Personal"
        _isTitleValid.value = false
        _isDescriptionValid.value = false
        _titleTouched.value = false
        _descriptionTouched.value = false
        _selectedFamilyGroupId.value = null
        _assignedUserIds.value = emptyList()
    }

    fun getTitleErrorMessage(): String? {
        return if (_titleTouched.value && _taskTitle.value.isEmpty()) {
            "Title cannot be empty"
        } else if (_titleTouched.value && _taskTitle.value.length < 3) {
            "Title must be at least 3 characters"
        } else null
    }

    fun getDescriptionErrorMessage(): String? {
        return if (_descriptionTouched.value && _taskDescription.value.length > 500) {
            "Description must be less than 500 characters"
        } else null
    }
}
