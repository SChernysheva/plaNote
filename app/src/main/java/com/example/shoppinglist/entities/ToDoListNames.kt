package com.example.shoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "ToDoListNames")
data class ToDoListNames(
    @PrimaryKey (autoGenerate = true)
    val id:Int?,

    @ColumnInfo (name= "name")
    val name:String,

    @ColumnInfo (name="important")
    var important: Boolean = false,

    @ColumnInfo (name="completed")
    var completed: Boolean = false,

    @ColumnInfo(name="time")
    val time: String
): java.io.Serializable

