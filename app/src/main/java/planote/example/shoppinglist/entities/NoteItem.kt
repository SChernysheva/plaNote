package planote.example.shoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "Note_List")

data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,

    @ColumnInfo (name = "title")
    val title:String,

    @ColumnInfo(name = "description")
    val description:String,

    @ColumnInfo(name = "time")
    val time:String,

    @ColumnInfo(name = "category")
    val category:String,

): Serializable