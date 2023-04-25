package planote.example.shoppinglist.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import planote.example.shoppinglist.Fragments.FragmentManager
import planote.example.shoppinglist.Fragments.NoteFragment
import planote.example.shoppinglist.Fragments.TasksFragment
import planote.example.shoppinglist.Settings.SettingsActivity
import planote.example.shoppinglist.billing.BillingManage
import com.planote.shoppinglist.R
import com.planote.shoppinglist.databinding.ActivityMainBinding
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var currentMenuItemId = R.id.notes
    private var currentTheme = ""
    lateinit var defPref:SharedPreferences
    private var iAd: com.yandex.mobile.ads.interstitial.InterstitialAd? = null
    private var adCount:Int = 0
    private var adCountMax:Int = 7
    private lateinit var sharpref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityMainBinding.inflate(layoutInflater)
        defPref=PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = defPref.getString("theme_key", "Dark").toString()
        setTheme(
            if(defPref.getString("theme_key", "Purple") == "Dark"){R.style.Theme_ShoppingList}
            else {R.style.Theme_ShoppingListDark}
        )
        getSupportActionBar()?.hide()

        super.onCreate(savedInstanceState)
        sharpref = getSharedPreferences("main_pref", MODE_PRIVATE)
        setContentView(binding.root)
        FragmentManager.setFragment(NoteFragment.newInstance(), this)
        setBottomNavListener()
        load()
    }

    override fun onResume() {
        super.onResume()
        if(defPref.getString("theme_key", "Dark")!=currentTheme) recreate()
        binding.bNav.selectedItemId =currentMenuItemId
    }

    private fun setBottomNavListener() {
        binding.bNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.notes -> {
                    showAd(object : AdListener {
                        override fun onFinish() {
                            FragmentManager.setFragment(NoteFragment.newInstance(), this@MainActivity)
                            currentMenuItemId = R.id.notes
                        }
                    })
                    }

                R.id.toDoList -> {
                    FragmentManager.setFragment(TasksFragment.newInstance(), this)
                    currentMenuItemId = R.id.toDoList
                }
                R.id.settings ->
                {
                    showAd(object : AdListener {
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        }
                    })
                }
            }
            true
        }
    }

    private fun load(){
            MobileAds.initialize(this, object : InitializationListener {
                override fun onInitializationCompleted() {
                }
            })
            iAd = com.yandex.mobile.ads.interstitial.InterstitialAd(this)
            iAd!!.setAdUnitId(getString(R.string.inter_ad_id))
            val request: com.yandex.mobile.ads.common.AdRequest =
                com.yandex.mobile.ads.common.AdRequest.Builder().build()
            iAd!!.loadAd(request)
        }


    private fun showAd(adlistener: AdListener){
        if (iAd!=null && adCount>=adCountMax ){
            iAd?.setInterstitialAdEventListener(object: InterstitialAdEventListener{
                override fun onAdLoaded() {
                }

                override fun onAdFailedToLoad(p0: AdRequestError) {
                    iAd=null
                    load()
                    adlistener.onFinish()
                }

                override fun onAdShown() {

                }

                override fun onAdDismissed() {
                    iAd=null
                    load()
                    adlistener.onFinish()
                }

                override fun onAdClicked() {

                }

                override fun onLeftApplication() {

                }

                override fun onReturnedToApplication() {

                }

                override fun onImpression(p0: ImpressionData?) {

                }
            })
            iAd?.show()
            load()
            adCount=0
        } else {
            adlistener.onFinish()
            adCount++
        }
    }

    interface AdListener{
        fun onFinish()
    }



}