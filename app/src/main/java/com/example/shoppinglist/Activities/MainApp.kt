package com.example.shoppinglist.Activities

import android.app.Application
import com.example.shoppinglist.db.MainDataBase

class MainApp: Application() {
    val database by lazy { MainDataBase.getDB(this) }
}