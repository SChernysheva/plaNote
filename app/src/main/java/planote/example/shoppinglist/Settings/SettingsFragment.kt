package planote.example.shoppinglist.Settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.planote.shoppinglist.R
import planote.example.shoppinglist.billing.BillingManage

class SettingsFragment: PreferenceFragmentCompat() {
    //private lateinit var removeAdsPref: Preference
    private lateinit var defPref: SharedPreferences
    //private lateinit var bManager: BillingManage
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        //init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        defPref= PreferenceManager.getDefaultSharedPreferences(requireContext())
        activity?.setTheme(
            if(defPref.getString("theme_key", "Purple") == "Purple"){R.style.Theme_ShoppingList}
            else {R.style.Theme_ShoppingListDark}

        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

//     private fun init(){
//        bManager = BillingManage(activity as AppCompatActivity)
//        removeAdsPref=findPreference<Preference>("remove_ads")!!
//        removeAdsPref.setOnPreferenceClickListener {
//            bManager.startConnect()
//            true
//        }
//    }



}