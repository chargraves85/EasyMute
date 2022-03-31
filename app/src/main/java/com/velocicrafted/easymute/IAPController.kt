package com.velocicrafted.easymute

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class IAPController(private val context: Context, private val activity: Activity) : PurchasesUpdatedListener {


    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()


    private fun connectToBilling() {

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    Log.v("INAAP", "Billing Connected.")
                } else {
                    Log.v("INAAP", "Billing unable to connect. ${billingResult.responseCode}")

                }
            }

            override fun onBillingServiceDisconnected() {
                Log.v("INAPP", "Billing Disconnected.")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })

    }

    suspend fun startPurchaseFlow() {

        connectToBilling()

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
        }
    }

    private fun handlePurchase(purchase: Purchase) {

        AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // if purchase is acknowledged
                // TODO: Grant entitlement to the user. and restart activity

                Toast.makeText(context, "Item Purchased", Toast.LENGTH_SHORT).show()
                recreate(activity)
            }
        }

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
            }
        }
    }
}