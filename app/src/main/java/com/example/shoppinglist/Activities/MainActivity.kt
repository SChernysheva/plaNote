package com.example.shoppinglist.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.media.tv.AdRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.widget.ToolbarWidgetWrapper
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.shoppinglist.Fragments.FragmentManager
import com.example.shoppinglist.Fragments.NoteFragment
import com.example.shoppinglist.Fragments.TasksFragment
import com.example.shoppinglist.R
import com.example.shoppinglist.Settings.SettingsActivity
import com.example.shoppinglist.billing.BillingManage
import com.example.shoppinglist.databinding.ActivityMainBinding
import com.google.android.material.internal.ToolbarUtils

import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var currentMenuItemId = R.id.notes
    private var currentTheme = ""
    lateinit var defPref:SharedPreferences
    private var iAd: com.yandex.mobile.ads.interstitial.InterstitialAd? = null
    private var adCount:Int = 0
    private var adCountMax:Int = 6
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
        if(sharpref.getBoolean(BillingManage.REMOVE_AD_KEY, false)) load()
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
                    showAd(object : AdListener{
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
        if (iAd!=null && adCount>=adCountMax){
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
            adCountMax++
        }
    }

    interface AdListener{
        fun onFinish()
    }



}