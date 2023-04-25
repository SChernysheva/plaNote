package planote.example.shoppinglist.Activities

import android.app.Application
import planote.example.shoppinglist.db.MainDataBase

class MainApp: Application() {
    val database by lazy { MainDataBase.getDB(this) }
}