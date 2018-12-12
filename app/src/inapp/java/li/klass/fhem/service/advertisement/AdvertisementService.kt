/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2011, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLIC LICENSE, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 * for more details.
 *
 * You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 * along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 *   Boston, MA  02110-1301  USA
 */

package li.klass.fhem.service.advertisement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import li.klass.fhem.AndFHEMApplication
import li.klass.fhem.R
import li.klass.fhem.activities.PremiumActivity
import li.klass.fhem.constants.Actions
import li.klass.fhem.constants.BundleExtraKeys
import li.klass.fhem.constants.BundleExtraKeys.IS_PREMIUM
import li.klass.fhem.constants.ResultCodes.SUCCESS
import li.klass.fhem.service.intent.LicenseIntentService
import li.klass.fhem.util.FhemResultReceiver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdvertisementService @Inject
constructor() {
    private var lastErrorTimestamp: Long = 0

    fun addAd(view: View, activity: Activity) {

        activity.sendBroadcast(Intent(Actions.IS_PREMIUM)
                .setClass(activity, LicenseIntentService::class.java)
                .putExtra(BundleExtraKeys.RESULT_RECEIVER, object : FhemResultReceiver() {
                    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                        if (!resultData!!.containsKey(IS_PREMIUM)) {
                            return
                        }
                        val isPremium = resultCode == SUCCESS && resultData.getBoolean(IS_PREMIUM, false)
                        showAdsBasedOnPremium(isPremium, view, activity)
                    }
                })
        )
    }

    private fun showAdsBasedOnPremium(isPremium: Boolean, view: View, activity: Activity) {
        var showAds = true
        val adContainer = view.findViewById<View>(R.id.adContainer) as LinearLayout

        Log.i(TAG, "isPremium is $isPremium")
        if (isPremium) {
            showAds = false
            Log.i(TAG, "found premium version, skipping ads")
        }

        val api = GoogleApiAvailability.getInstance();
        if (!showAds) {
            adContainer.visibility = View.GONE
        } else if (api.isGooglePlayServicesAvailable(activity) != ConnectionResult.SUCCESS) {
            addErrorView(activity, adContainer)
            Log.e(TAG, "cannot find PlayServices")
        } else {
            adContainer.visibility = View.VISIBLE
            adContainer.removeAllViews()

            if (System.currentTimeMillis() - lastErrorTimestamp < 1000 * 60 * 10) {
                addErrorView(activity, adContainer)
                Log.i(TAG, "still in timeout, showing error view")
                return
            }

            Log.i(TAG, "showing ad")

            val adView = AdView(activity)
            adView.adUnitId = AndFHEMApplication.AD_UNIT_ID
            adView.adSize = AdSize.SMART_BANNER

            addListener(activity, adContainer, adView)
            adView.loadAd(AdRequest.Builder().build())
            adContainer.addView(adView)
        }
    }

    private fun addListener(activity: Activity, adContainer: LinearLayout, adView: AdView) {
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)

                adContainer.removeAllViews()
                addErrorView(activity, adContainer)
                lastErrorTimestamp = System.currentTimeMillis()
                Log.i(TAG, "failed to receive ads, showing error view")
            }
        }
    }

    companion object {
        private val TAG = AdvertisementService::class.java.name

        private fun addErrorView(activity: Activity, container: LinearLayout) {
            val selfAd = activity.layoutInflater.inflate(R.layout.selfad, container, false) as ImageView
            selfAd.setOnClickListener {
                val intent = Intent(activity, PremiumActivity::class.java)
                activity.startActivity(intent)
            }
            container.addView(selfAd)
        }
    }
}
