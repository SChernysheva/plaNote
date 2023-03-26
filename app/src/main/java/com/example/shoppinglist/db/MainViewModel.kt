package com.example.shoppinglist.db

import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.shoppinglist.entities.NoteItem
import com.example.shoppinglist.entities.ToDoListNames
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainViewModel(database: MainDataBase,
                    private val savedStateHandle: SavedStateHandle): ViewModel() {
    val dao = database.getDAO()

    var allTasks: LiveData<List<ToDoListNames>> = dao.getAllTasks().asLiveData()

    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allTasksSortByDate: LiveData<List<ToDoListNames>> = dao.getAllTasksSortbyDate().asLiveData()


    fun insertNotes(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }
    fun insertTasks(task: ToDoListNames)= viewModelScope.launch {
        dao.insertTask(task)
    }


    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }
    fun deleteTask(id: Int) = viewModelScope.launch {
        dao.deleteTask(id)
    }

    fun deleteAllTasks() = viewModelScope.launch {
        dao.deleteAllTasks()
    }

    fun deleteCompletedTasks() = viewModelScope.launch {
        dao.deleteCompleted()
    }



    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }
    fun updateTask(task: ToDoListNames) = viewModelScope.launch {
        dao.updateTask(task)
    }


    fun searchDataBase(query: String): LiveData<List<NoteItem>> {
        return dao.searchDataBase(query).asLiveData()
    }


    class ViewModelFactory(owner: SavedStateRegistryOwner, val database: MainDataBase): AbstractSavedStateViewModelFactory(owner, null) {


        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle,
        ): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress ("UNCHECKED_CAST")
                return MainViewModel(database, handle) as T
            }
            throw java.lang.IllegalArgumentException("Unknown ViewModelClass")
        }
    }

}