package ie.setu.tazq.ui.components.tasklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ie.setu.tazq.data.Task
import ie.setu.tazq.data.model.TaskModel

@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    onDeleteTask: (Task) -> Unit,
    onEditTask: (Task) -> Unit,
    onToggleTask: (Task) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tasks,
            key = { task -> task.id }
        ) { task ->
            TaskCard(
                task = task,
                onDeleteTask = { onDeleteTask(task) },
                onEditTask = { onEditTask(task) },
                onToggleTask = { onToggleTask(task) }
            )
        }
    }
}

@Composable
fun TaskModelList(
    modifier: Modifier = Modifier,
    tasks: List<TaskModel>,
    onDeleteTask: (TaskModel) -> Unit,
    onEditTask: (TaskModel) -> Unit,
    onToggleTask: (TaskModel) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tasks,
            key = { task -> task._id }
        ) { task ->
            TaskModelCard(
                task = task,
                onDeleteTask = { onDeleteTask(task) },
                onEditTask = { onEditTask(task) },
                onToggleTask = { onToggleTask(task) }
            )
        }
    }
}
