package ie.setu.tazq.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ie.setu.tazq.data.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Query("SELECT * FROM task WHERE userId = :userId")
    fun getAll(userId: String): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id=:id AND userId = :userId")
    fun get(id: Int, userId: String): Flow<Task>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("UPDATE task SET isDone = :isDone WHERE id = :id AND userId = :userId")
    suspend fun updateTaskStatus(id: Int, isDone: Boolean, userId: String)

    @Delete
    suspend fun delete(task: Task)

    @Query("UPDATE task SET isDone = :isDone WHERE id = :id")
    suspend fun updateTaskStatus(id: Int, isDone: Boolean)
}
