package planote.example.shoppinglist.Fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import planote.example.shoppinglist.Activities.MainApp
import planote.example.shoppinglist.Activities.NewNoteActivity
import com.planote.shoppinglist.R
import planote.example.shoppinglist.db.MainViewModel
import planote.example.shoppinglist.entities.NoteItem
import com.planote.shoppinglist.databinding.FragNoteBinding
import planote.example.shoppinglist.db.Adapters.AdapterNoteRes


class NoteFragment() : BaseFragment(), AdapterNoteRes.Listener {
    private lateinit var binding: FragNoteBinding
    private lateinit var adapter: AdapterNoteRes
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var searchView:SearchView
    private lateinit var defPref: SharedPreferences
    val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory(
          this,  (context?.applicationContext as MainApp).database) }


    override fun onClickView() {
        editLauncher.launch(Intent(requireActivity(), NewNoteActivity::class.java))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        defPref= PreferenceManager.getDefaultSharedPreferences(requireContext())
        activity?.setTheme(
            if(defPref.getString("theme_key", "Dark") == "Purple"){R.style.Theme_ShoppingList}
            else {
                R.style.Theme_ShoppingListDark
            }
        )
        activity?.actionBar?.hide()
        binding = FragNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()
        activity?.actionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcview()
        observer()
        binding.buttonNewNote.setOnClickListener {
            FragmentManager.currentFrag?.onClickView()
        }
    }

    private fun initRcview() = with(binding) {

        rcView.layoutManager = if(defPref.getString("note_style_key", "Vertical") == "Vertical"){
            LinearLayoutManager(activity)
        } else { StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }
        adapter= AdapterNoteRes(this@NoteFragment)
        rcView.adapter = adapter
        searchListener()
    }

    private fun observer() {
        mainViewModel.allNotes.observe(viewLifecycleOwner) {
            adapter.setNew(it)
        }
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        {
            if (it.resultCode == Activity.RESULT_OK) {
                val editState= it.data?.getStringExtra(EDIT_STATE_KEY)
                if (editState=="new") {

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mainViewModel.insertNotes(it.data?.getSerializableExtra(DATA_KEY, NoteItem::class.java)!!)
                    } else{
                        mainViewModel.insertNotes(it.data?.getSerializableExtra(DATA_KEY) as NoteItem)
                    }

                } else if (editState=="update"){

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        mainViewModel.updateNote(it.data?.getSerializableExtra(DATA_KEY, NoteItem::class.java)!!)
                    } else {
                    mainViewModel.updateNote(it.data?.getSerializableExtra(DATA_KEY) as NoteItem) }
                }
            }
        }

    }



    override fun deleteNote(id: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete this element?")
        builder.setPositiveButton("Yes, I'm sure"){ _, _ -> mainViewModel.deleteNote(id)}
        builder.setNegativeButton("No") { dialogInterface, i -> }
        builder.show()
    }

    fun searchListener(){
        searchView=binding.search
        searchView.isSubmitButtonEnabled= true
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                searchDataBase(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                searchDataBase(query)
                return true
            }

            private fun searchDataBase(query: String){
                val searchQuery="%$query%"

                mainViewModel.searchDataBase(searchQuery).observe(viewLifecycleOwner) { list ->
                    list.let {
                        adapter.setNew(it)
                    }
                }
            }

        })
    }


    override fun updateNote(note: NoteItem) {
        val i = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(DATA_KEY, note)
        }
        editLauncher.launch(i)
    }

    private fun dialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete this element?")
        builder.setPositiveButton("Yes, I'm sure", { dialogInterface, i -> mainViewModel.deleteNote(id)})
        builder.setPositiveButton("No", { dialogInterface, i -> })

    }


    companion object {
        const val DATA_KEY = "data_key"
        const val EDIT_STATE_KEY="edit_state"

        @JvmStatic
        fun newInstance() = NoteFragment()
    }

}


