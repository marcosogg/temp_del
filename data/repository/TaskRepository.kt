package ie.setu.tazq.data.repository

import ie.setu.tazq.data.Task
import ie.setu.tazq.data.room.TaskDAO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDAO: TaskDAO) {
    fun getAll(userId: String): Flow<List<Task>> = taskDAO.getAll(userId)
    fun get(id: Int, userId: String): Flow<Task> = taskDAO.get(id, userId)

    suspend fun insert(task: Task) = taskDAO.insert(task)
    suspend fun update(task: Task) = taskDAO.update(task)
    suspend fun delete(task: Task) = taskDAO.delete(task)

    suspend fun updateTaskStatus(id: Int, isDone: Boolean, userId: String) {
        taskDAO.updateTaskStatus(id, isDone, userId)
    }
}
