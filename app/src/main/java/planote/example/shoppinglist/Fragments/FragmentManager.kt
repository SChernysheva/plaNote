package planote.example.shoppinglist.Fragments

import androidx.appcompat.app.AppCompatActivity
import com.planote.shoppinglist.R

object FragmentManager {
    var  currentFrag: BaseFragment?=null


    fun setFragment(newFrag: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeholder1, newFrag)
        transaction.commit()
        currentFrag =newFrag
    }
}