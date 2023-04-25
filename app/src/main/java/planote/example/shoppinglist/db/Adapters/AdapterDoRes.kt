package planote.example.shoppinglist.db.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import planote.example.shoppinglist.Fragments.TasksFragment
import com.planote.shoppinglist.R
import com.planote.shoppinglist.databinding.ItemTaskBinding
import planote.example.shoppinglist.entities.ToDoListNames

class AdapterDoRes(private val listener: TasksFragment, currentTheme:String): RecyclerView.Adapter<AdapterDoRes.ViewHolder>() {
    var mTasksList: List<ToDoListNames> = ArrayList()
    val CT = currentTheme


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemTaskBinding.bind(view)

        fun setData(task: ToDoListNames, listener: TasksFragment, currentTheme: String) = with(binding) {
            nameTask.text = task.name
            time.text=task.time
            checkboxComleted.isChecked = task.completed
            if (task.important) {
                priority.text="High"
                binding.deleteTask.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.high_pr))
                myLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.high_pr))

                binding.nameTask.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.time.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.textPri.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.priority.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.deleteTask.setImageResource(R.drawable.baseline_delete_24)
            } else {
                priority.text="Low"
                if (currentTheme=="BW") {
                    binding.deleteTask.setImageResource(R.drawable.baseline_delete_24black)
                    binding.deleteTask.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    myLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))

                    binding.nameTask.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.time.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.textPri.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    binding.priority.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))

                } else if (currentTheme=="Purple"){
                binding.deleteTask.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.low_pr))
                myLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.low_pr))
                    }
            }
            deleteTask.setOnClickListener {
                listener.deleteTask(task.id!!)
            }
            itemView.setOnClickListener {
                listener.updateTask(task)
            }
            SetPaintFlag(binding, currentTheme)
            checkboxComleted.setOnClickListener {
                listener.onClickCheckBox(task.copy(completed = checkboxComleted.isChecked))
            }



        }


        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    LayoutInflater.from(
                        parent.context
                    ).inflate(R.layout.item_task, parent, false)
                )
            }

        }
        fun SetPaintFlag(binding: ItemTaskBinding, currentTheme: String){
            if (binding.checkboxComleted.isChecked){
                binding.myLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.gray))
                binding.deleteTask.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.gray))
            } else {
                if (binding.priority.text=="High") {
                    binding.myLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.high_pr))
                    binding.deleteTask.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.high_pr))
                } else {
                    if (currentTheme == "Purple") {
                    binding.myLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.low_pr))
                    binding.deleteTask.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.low_pr))
                    } else {
                        binding.myLayout.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                        binding.deleteTask.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return mTasksList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var task= mTasksList[position]
        holder.setData(task, listener, CT )
    }

    fun setNew(newList: List<ToDoListNames>){
        mTasksList=newList
        notifyDataSetChanged()
    }






    interface Listener{
        fun deleteTask(id:Int)
        fun updateTask(task: ToDoListNames)
    }

}