package planote.example.shoppinglist.Fragments

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.savedstate.SavedStateRegistryOwner
import planote.example.shoppinglist.Activities.MainApp
import planote.example.shoppinglist.Activities.NewTaskActivity
import com.planote.shoppinglist.R
import com.planote.shoppinglist.databinding.FragToDoListBinding
import planote.example.shoppinglist.db.Adapters.AdapterDoRes
import planote.example.shoppinglist.db.MainViewModel
import planote.example.shoppinglist.entities.ToDoListNames

class TasksFragment: BaseFragment(), AdapterDoRes.Listener {
    private lateinit var binding: FragToDoListBinding
    private lateinit var adapter: AdapterDoRes
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var defPref: SharedPreferences
    private var currentTheme=""
    val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.ViewModelFactory(
           this, (context?.applicationContext as MainApp).database
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        requireActivity().actionBar?.show()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        onEditResult()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_menu, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_completed -> { dialogDeleteCompleted()
                true }
            R.id.delete_all -> { dialogAll()
                true }
            R.id.sort_by_completed ->{ mainViewModel.allTasks=mainViewModel.allTasks
                observer()
                true}
            R.id.sort_by_date -> { mainViewModel.allTasks=mainViewModel.allTasksSortByDate
                observer()
                true
            }
            else -> {super.onOptionsItemSelected(item)}
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.actionBar?.show()
        defPref= PreferenceManager.getDefaultSharedPreferences(requireContext())
        activity?.setTheme(
            if(defPref.getString("theme_key", "Dark") == "Purple"){R.style.Theme_ShoppingList}
            else {
                R.style.Theme_ShoppingListDark
            }
        )
        if(defPref.getString("theme_key", "Dark") == "Purple") currentTheme="Purple"
        else currentTheme="BW"
        binding = FragToDoListBinding.inflate(inflater, container, false)



        val myCallback = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {return false}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task: ToDoListNames = adapter.mTasksList[viewHolder.adapterPosition]
                onClickCheckBox(task.copy(completed = (!(task.completed))))
            }
        }

        val myCallback2 = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {return false}

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task: ToDoListNames = adapter.mTasksList[viewHolder.adapterPosition]
                deleteTask(task.id!!)
            }
        }

        val myHelper = ItemTouchHelper(myCallback)
        myHelper.attachToRecyclerView(binding.rcViewTODO)

        val myHelper2 = ItemTouchHelper(myCallback2)
        myHelper2.attachToRecyclerView(binding.rcViewTODO)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcview()
        observer()
        binding.buttonNewTodo.setOnClickListener {
            FragmentManager.currentFrag?.onClickView()
        }
    }


    private fun initRcview() = with(binding) {
        rcViewTODO.layoutManager=LinearLayoutManager(activity)
        adapter = AdapterDoRes(this@TasksFragment, currentTheme)
        rcViewTODO.adapter = adapter
    }

    override fun deleteTask(id: Int) {
        mainViewModel.deleteTask(id)
    }

    override fun updateTask(task: ToDoListNames) {
        val i = Intent(activity, NewTaskActivity::class.java).apply {
            putExtra(DATA_KEY, task)
        }
        editLauncher.launch(i)
    }

    fun onClickCheckBox(task: ToDoListNames){
        mainViewModel.updateTask(task)
    }


    private fun observer() {
        mainViewModel.allTasks.observe(
            viewLifecycleOwner
        ) {
            adapter.setNew(it)
        }
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val editState= it.data?.getStringExtra(EDIT_STATE_KEY)
                if (editState=="new") {

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mainViewModel.insertTasks(it.data?.getSerializableExtra(DATA_KEY, ToDoListNames::class.java)!!)
                    } else{
                        mainViewModel.insertTasks(it.data?.getSerializableExtra(DATA_KEY) as ToDoListNames)
                    }


                } else if (editState=="update"){

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        mainViewModel.updateTask(it.data?.getSerializableExtra(DATA_KEY, ToDoListNames::class.java)!!)
                    } else {
                        mainViewModel.updateTask(it.data?.getSerializableExtra(DATA_KEY) as ToDoListNames) }
                }
            }
        }

    }

    override fun onClickView() {
        editLauncher.launch(Intent(requireActivity(), NewTaskActivity::class.java))
    }

    fun dialogAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete all elements?")
        builder.setPositiveButton("Yes, I'm sure"){ _, _ -> mainViewModel.deleteAllTasks()}
        builder.setNegativeButton("No") { dialogInterface, i -> }
        builder.show()
    }

    fun dialogDeleteCompleted() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete completed elements?")
        builder.setPositiveButton("Yes, I'm sure"){ _, _ -> mainViewModel.deleteCompletedTasks()}
        builder.setNegativeButton("No") { dialogInterface, i -> }
        builder.show()
    }

    fun help() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Swipes")
        builder.setMessage("Swipe left to delete task, swipe right to complete task")
        builder.setPositiveButton("Ok!") { _, _ -> }
        builder.show()
    }



    companion object {
        const val DATA_KEY = "data_key"
        const val EDIT_STATE_KEY="edit_state"
        @JvmStatic
        fun newInstance() = TasksFragment()
    }

}