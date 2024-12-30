package ie.setu.tazq.ui.components.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ie.setu.tazq.data.Task
import ie.setu.tazq.ui.viewmodel.FamilyGroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(
    modifier: Modifier = Modifier,
    onTaskCreated: (Task) -> Unit,
    familyGroupViewModel: FamilyGroupViewModel = hiltViewModel()
) {
    var taskTitle by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var taskDescription by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Personal") }
    var selectedFamilyGroupId by remember { mutableStateOf<String?>(null) }
    val familyGroups by familyGroupViewModel.familyGroups.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskInput(
            value = taskTitle,
            onTaskTitleChange = { taskTitle = it },
            isError = taskTitle.isNotEmpty() && taskTitle.length < 3,
            errorMessage = if (taskTitle.isNotEmpty() && taskTitle.length < 3) {
                "Title must be at least 3 characters"
            } else null
        )

        RadioButtonGroup(
            selectedPriority = taskPriority,
            onPriorityChange = { taskPriority = it }
        )

        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            modifier = Modifier.fillMaxWidth()
        )

        TaskDescription(
            value = taskDescription,
            onDescriptionChange = { taskDescription = it },
            isError = taskDescription.length > 500,
            errorMessage = if (taskDescription.length > 500) {
                "Description must be less than 500 characters"
            } else null
        )

        // Family Group Selection
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                readOnly = true,
                value = selectedFamilyGroupId ?: "Select Family Group",
                onValueChange = { },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    onClick = {
                        selectedFamilyGroupId = null
                        expanded = false
                    },
                    text = { Text("None (Private Task)") }
                )
                familyGroups.forEach { group ->
                    DropdownMenuItem(
                        onClick = {
                            selectedFamilyGroupId = group.groupId
                            expanded = false
                        },
                        text = { Text(group.groupName) }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (taskTitle.length >= 3 && taskDescription.length <= 500) {
                    val newTask = Task(
                        title = taskTitle,
                        priority = taskPriority,
                        description = taskDescription,
                        category = selectedCategory,
                        familyGroupId = selectedFamilyGroupId
                    )
                    onTaskCreated(newTask)
                    // Reset form
                    taskTitle = ""
                    taskDescription = ""
                    taskPriority = TaskPriority.MEDIUM
                    selectedCategory = "Personal"
                    selectedFamilyGroupId = null
                }
            },
            enabled = taskTitle.length >= 3 && taskDescription.length <= 500,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}
