package com.example.shoppinglist.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.shoppinglist.Fragments.NoteFragment
import com.example.shoppinglist.Fragments.TasksFragment
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityAddedEditTaskBinding
import com.example.shoppinglist.entities.NoteItem
import com.example.shoppinglist.entities.ToDoListNames
import java.io.Serializable
import java.util.*

class NewTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddedEditTaskBinding
    private var task: ToDoListNames ?= null
    lateinit var defPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        defPref= PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(
            if(defPref.getString("theme_key", "Dark") == "Purple"){R.style.Theme_ShoppingList}
            else {
                R.style.Theme_ShoppingListDark
            }
        )
        super.onCreate(savedInstanceState)
        binding= ActivityAddedEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTask()
        binding.saveTask.setOnClickListener{
            setMainResult()
        }
        binding.cancel.setOnClickListener {
            finish()
        }
    }

    private fun getTask(){
        val sTask=getSerializable(TasksFragment.DATA_KEY, ToDoListNames::class.java)
        if (sTask!=null) {
            task=sTask as ToDoListNames
            binding.editTaskName.setText(task?.name)
            binding.checkBoxImportant.isChecked= task!!.important
        }
    }

    private fun setMainResult() {
        var editstate="new"
        val tempTask: ToDoListNames?
        if (task==null){
            tempTask=createTask()
        } else{
            tempTask=updateTask()
            editstate="update"
        }
        val i= Intent().apply {
            putExtra(TasksFragment.DATA_KEY, tempTask)
            putExtra(TasksFragment.EDIT_STATE_KEY, editstate)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun createTask(): ToDoListNames{
        return ToDoListNames(id = null, name=binding.editTaskName.text.toString(),
        important = binding.checkBoxImportant.isChecked, completed = false, time = getTime())
    }

    private fun updateTask(): ToDoListNames?{
        return task?.copy(name = binding.editTaskName.text.toString(), important = binding.checkBoxImportant.isChecked )
    }

    private fun getTime():String {
        val formatter = SimpleDateFormat("hh:mm:ss - EEE, MMM d, yy", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    fun <T : Serializable?> getSerializable(name: String, clazz: Class<T>): Serializable? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(name, clazz)
        else
            intent.getSerializableExtra(name)
    }
}