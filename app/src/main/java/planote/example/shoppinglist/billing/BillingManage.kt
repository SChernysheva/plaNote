package planote.example.shoppinglist.billing

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchaseState
import planote.example.shoppinglist.Activities.MainActivity

class BillingManage(val activity: AppCompatActivity) {
    private var bClient: BillingClient? = null

    init {
        setUpbClient()
    }

    private fun setUpbClient(){
        bClient = BillingClient.newBuilder(activity)
            .setListener(getListener())
            .enablePendingPurchases()
            .build()
    }

    private fun getListener(): PurchasesUpdatedListener{
        return PurchasesUpdatedListener{bResult, list ->
            run {
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) }
                }
            }
        }
    }


    public fun startConnect(){
        bClient?.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
            }

            override fun onBillingSetupFinished(p0: BillingResult) {

            }

        })
    }

    private fun getItem(){
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_AD_ITEM)
        val skuDetails = SkuDetailsParams.newBuilder()
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        bClient?.querySkuDetailsAsync(skuDetails.build()){
            bResult, list ->
            run {
                if(bResult.responseCode==BillingClient.BillingResponseCode.OK){
                    if (list!=null){
                        if (list.isNotEmpty()){
                            val bFlowParams =
                                BillingFlowParams.newBuilder().setSkuDetails(list[0]).build()
                            bClient?.launchBillingFlow(activity, bFlowParams)
                        }
                    }
                }
            }
        }
    }

    private fun nonConsumableItem(purchase: Purchase){
        if (purchase.purchaseState== Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged){
                val akParams = AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                bClient?.acknowledgePurchase(akParams){
                    if (it.responseCode == BillingClient.BillingResponseCode.OK){
                        savePref(true)
                        Toast.makeText(activity, "Successfully :)", Toast.LENGTH_LONG).show()
                    }
                    else {
                        savePref(false)
                        Toast.makeText(activity, "Unsuccessfully :(", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun closeConnect(){
        bClient?.endConnection()
    }

    private fun savePref(isPurchase: Boolean){
        val pref = activity.getSharedPreferences("main_pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_AD_KEY, isPurchase)
        editor.apply()
    }

    companion object{
        const val REMOVE_AD_ITEM = "remove_ad_item_id"
        const val REMOVE_AD_KEY = "remove_ad_key"
    }
}