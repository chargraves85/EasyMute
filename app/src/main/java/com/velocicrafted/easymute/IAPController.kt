package com.velocicrafted.easymute

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class IAPController(private val activity: Activity, private val context: Context) : PurchasesUpdatedListener, AppCompatActivity() {

    private val settings = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()


    fun connectToBilling() {

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    Log.v("INAAP", "Billing Connected.")
                    runBlocking {
                        launch {
                            startPurchaseFlow()
                        }
                    }
                } else {
                    Log.v("INAAP", "Billing unable to connect. ${billingResult.responseCode}")
                }
            }

            override fun onBillingServiceDisconnected() {
                connectToBilling()
            }
        })
    }

    private suspend fun startPurchaseFlow() {

        val skuList = ArrayList<String>()
        skuList.add("premium_upgrade")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        // leverage querySkuDetails Kotlin extension function
        withContext(Dispatchers.IO) {
            billingClient.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
                if (responseCode.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.v("INAPP", "Successful sku query. $responseCode")
                    if (skuDetailsList != null) {
                        launchBillingFlow(skuDetailsList)
                    } else {
                        Log.v("INAPP", "No sku details found.")
                    }
                } else {
                    Log.v("INAPP", "Failed sku query. $responseCode")
                }
            }
        }
    }



    private fun launchBillingFlow(skuDetailsList: MutableList<SkuDetails>) {

        if (skuDetailsList.size == 1) {
            val premiumSku = skuDetailsList[0]

            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(premiumSku)
                .build()

            val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode

            if (responseCode != 200) {
                Log.v("INAPP", "Unable to launch billing flow. $responseCode")
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.v("INAPP", "User canceled transaction. ${billingResult.responseCode}")
        } else if  (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
            settings.edit().putBoolean("premiumEnabled", true).apply()
            recreate(activity)
        }
    }

    private fun handlePurchase(purchase: Purchase) {

        val ackListener = AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // if purchase is acknowledged
                settings.edit().putBoolean("premiumEnabled", true).apply()

                Toast.makeText(context, "Item Purchased", Toast.LENGTH_SHORT).show()
                recreate(activity)
            }
        }

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                runBlocking {
                    launch {
                        val ackPurchaseResult = withContext(Dispatchers.IO) {
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build(), ackListener)
                        }
                    }
                }
            }
        }

        // TODO: BillingClient.queryPurchasesAsync()

    }
}