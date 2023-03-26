package com.example.shoppinglist.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shoppinglist.entities.NoteItem
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao{
    @Query("SELECT * FROM Note_List")
    fun getAllNotes():Flow<List<NoteItem>>

    @Insert
    suspend fun insertNote(note: NoteItem)

    @Update
    suspend fun updateNote(note: NoteItem)

    @Query("DELETE FROM Note_List WHERE id IS :id")
    suspend fun deleteNote(id:Int)
    @Query("SELECT * FROM Note_List WHERE title LIKE :query OR description LIKE :query")
    fun searchDataBase(query: String): Flow<List<NoteItem>>

    @Query("SELECT * FROM ToDoListNames ORDER BY completed, important DESC")
    fun getAllTasks():Flow<List<ToDoListNames>>

    @Query("SELECT * FROM ToDoListNames ORDER BY time")
    fun getAllTasksSortbyDate():Flow<List<ToDoListNames>>


    @Insert
    suspend fun insertTask(task: ToDoListNames)

    @Update
    suspend fun updateTask(task: ToDoListNames)

    @Query("DELETE FROM ToDoListNames WHERE id IS :id")
    suspend fun deleteTask(id:Int)

    @Query("DELETE FROM ToDoListNames")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM ToDoListNames WHERE completed IS 1")
    suspend fun deleteCompleted()




}