package ie.setu.tazq.ui.screens.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ie.setu.tazq.ui.components.task.CategoryDropdown
import ie.setu.tazq.ui.components.task.RadioButtonGroup
import ie.setu.tazq.ui.components.task.TaskDescription
import ie.setu.tazq.ui.components.task.TaskInput

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val taskTitle by viewModel.taskTitle.collectAsState()
    val taskDescription by viewModel.taskDescription.collectAsState()
    val priority by viewModel.taskPriority.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isTitleValid by viewModel.isTitleValid.collectAsState()
    val isDescriptionValid by viewModel.isDescriptionValid.collectAsState()
    val showConfirmation by viewModel.showConfirmation.collectAsState()
    val titleTouched by viewModel.titleTouched
    val descriptionTouched by viewModel.descriptionTouched

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.hideConfirmation() },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Task Created!",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = "Your task has been added successfully.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.hideConfirmation() }
                ) {
                    Text("OK")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TaskInput(
            value = taskTitle,
            onTaskTitleChange = { viewModel.updateTaskTitle(it) },
            isError = titleTouched && !isTitleValid,
            errorMessage = if (titleTouched) viewModel.getTitleErrorMessage() else null
        )

        RadioButtonGroup(
            selectedPriority = priority,
            onPriorityChange = { viewModel.updateTaskPriority(it) }
        )

        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.updateSelectedCategory(it) },
            modifier = Modifier.fillMaxWidth()
        )

        TaskDescription(
            value = taskDescription,
            onDescriptionChange = { viewModel.updateTaskDescription(it) },
            isError = descriptionTouched && !isDescriptionValid,
            errorMessage = if (descriptionTouched) viewModel.getDescriptionErrorMessage() else null
        )

        Button(
            onClick = { viewModel.addTask() },
            enabled = viewModel.isFormValid(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}
