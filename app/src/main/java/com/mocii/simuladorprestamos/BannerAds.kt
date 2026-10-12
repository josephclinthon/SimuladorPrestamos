package com.mocii.simuladorprestamos

import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

object BannerAds {
    fun attach(fragment: Fragment, container: FrameLayout) {
        val owner = fragment.viewLifecycleOwner
        var ad: AdView? = null
        var destroyed = false
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) { ad?.resume() }
            override fun onPause(owner: LifecycleOwner) { ad?.pause() }
            override fun onDestroy(owner: LifecycleOwner) {
                destroyed = true
                container.removeAllViews()
                ad?.destroy()
                ad = null
                owner.lifecycle.removeObserver(this)
            }
        })
        container.doOnLayout {
            if (destroyed || container.width <= 0) return@doOnLayout
            val context = container.context
            val width = ((container.width - container.paddingLeft - container.paddingRight) /
                context.resources.displayMetrics.density).toInt()
            if (width <= 0) return@doOnLayout
            val banner = AdView(context).apply {
                adUnitId = context.getString(R.string.admob_banner_unit_id)
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width))
            }
            ad = banner
            container.addView(banner, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
            MobileAds.initialize(context.applicationContext) {
                container.post {
                    if (!destroyed) {
                        banner.loadAd(AdRequest.Builder().build())
                        if (!owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) banner.pause()
                    }
                }
            }
        }
    }
}
