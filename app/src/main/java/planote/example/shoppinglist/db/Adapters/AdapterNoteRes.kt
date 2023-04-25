package planote.example.shoppinglist.db.Adapters

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import planote.example.shoppinglist.Fragments.NoteFragment
import planote.example.shoppinglist.entities.NoteItem
import com.planote.shoppinglist.R
import com.planote.shoppinglist.databinding.ItemNoteBinding

class AdapterNoteRes(private val listener: NoteFragment): RecyclerView.Adapter<AdapterNoteRes.ViewHolder>() {
    private var mNotesList: List<NoteItem> = ArrayList()


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding= ItemNoteBinding.bind(view)
        fun setData(note: NoteItem, listener: Listener)= with(binding){
            tvTitle.text=note.title
            tvDecription.text=note.description
            tvData.text=note.time

            deleteNote.setOnClickListener { listener.deleteNote(note.id!!) }
            itemView.setOnClickListener { listener.updateNote(note) }
        }




        companion object{
            fun create(parent:ViewGroup): ViewHolder {
                return ViewHolder(LayoutInflater.from(
                    parent.context).inflate(R.layout.item_note, parent,false)
                )
            }

        }
    }

    fun setNew(newList: List<NoteItem>){
        mNotesList=newList
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return mNotesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var note= mNotesList[position]
        holder.setData(note, listener)
    }

    interface Listener{
        fun deleteNote(id:Int)
        fun updateNote(note: NoteItem)
    }

}