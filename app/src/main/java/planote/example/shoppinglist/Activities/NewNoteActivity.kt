package planote.example.shoppinglist.Activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import planote.example.shoppinglist.entities.NoteItem
import planote.example.shoppinglist.Fragments.NoteFragment
import com.planote.shoppinglist.R
import com.planote.shoppinglist.databinding.ActivityNewNoteBinding
import java.io.Serializable
import java.util.*

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem?= null
    private var pref: SharedPreferences? = null
    lateinit var defPref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        defPref= PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(
            if(defPref.getString("theme_key", "Dark") == "Purple"){R.style.Theme_ShoppingList}
            else {
                R.style.Theme_ShoppingListDark
            }
        )
        super.onCreate(savedInstanceState)
        binding=ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()
        getNote()
        binding.dataView.text=getKalendar()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        pref = PreferenceManager.getDefaultSharedPreferences(this)
        setTextSize()
    }

    private fun getNote(){
        val sNote=getSerializable(NoteFragment.DATA_KEY, NoteItem::class.java)
        if (sNote!=null) {
            note=sNote as NoteItem
            binding.edTittle.setText(note?.title)
            binding.edDescription.setText(note?.description)
            binding.dataView.text = getKalendar()
        }
    }

    private fun updatenote(): NoteItem?{
       return note?.copy(title = binding.edTittle.text.toString(),
        description = binding.edDescription.text.toString())
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId ==R.id.save) {
            setMainResult()
        }
        else if (item.itemId== android.R.id.home) {
            if ((note?.title.toString() != binding.edTittle.text.toString()) || (note?.description.toString() != binding.edDescription.text.toString())) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Save changes?")
                builder.setPositiveButton("Yes", { dialogInterface, i -> setMainResult() })
                builder.setNegativeButton("No", { dialogInterface, i -> finish()})
                builder.setNeutralButton("Cancel", {dialogInterface, i -> })
                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMainResult() {
        var editstate="new"
        val tempNote: NoteItem?
        if (note==null){
            tempNote=createNewNote()
        } else{
            tempNote=updatenote()
            editstate="update"
        }
        val i= Intent().apply {
            putExtra(NoteFragment.DATA_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editstate)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun createNewNote(): NoteItem {
        return NoteItem(null,
        binding.edTittle.text.toString(),
        binding.edDescription.text.toString(),
        getTime(),
        "")
    }
    private fun getTime():String {
        val formatter = SimpleDateFormat("hh:mm:ss - EEE, MMM d, yy", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    private fun getKalendar():String {
        val formatter = SimpleDateFormat("EEE, MMM d, yy", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    private fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    fun <T : Serializable?> getSerializable( name: String, clazz: Class<T>): Serializable? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(name, clazz)
        else
            intent.getSerializableExtra(name)
    }

    private fun setTextSize() = with(binding){
        edDescription.setTextSize(pref?.getString("text_size_key", "18"))
        edTittle.setTextSize(edDescription.textSize+3)
    }

    private fun EditText.setTextSize(size: String?){
        if (size != null) this.textSize = size.toFloat()

    }



    }
