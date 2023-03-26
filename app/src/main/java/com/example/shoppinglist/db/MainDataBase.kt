package com.example.shoppinglist.db

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shoppinglist.entities.*

@Database (entities = [NoteItem::class, ToDoListNames::class], version = 1)
abstract class MainDataBase: RoomDatabase(), ViewModelProvider.Factory {
    abstract fun getDAO(): Dao


    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null


        fun getDB(context: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "shop_list_db"
                ).build()
                instance
            }
        }
    }
}
