package com.mocii.simuladorprestamos

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Uses only Google's test units. Replace resources when production IDs are supplied. */
class RewardedSimulationAds(private val fragment: Fragment) {
    private var busy = false

    fun offer(onFinished: () -> Unit) {
        if (busy || !fragment.isAdded) return
        val state = DailySimulationQuota(fragment.requireContext()).today()
        if (state.unlimited) { onFinished(); return }
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle("Simulaciones de hoy")
            .setMessage("${state.description()}\n\nCompleta el anuncio ${state.ads + 1}/3 para avanzar hacia Full por hoy. Solo se cuenta al completarlo.")
            .setNegativeButton("Ahora no", null)
            .setPositiveButton("Ver anuncio") { _, _ -> watch { onFinished() } }.show()
    }

    fun watch(onFinished: (Boolean) -> Unit) {
        if (busy || !fragment.isAdded) { onFinished(false); return }
        busy = true
        val context = fragment.requireContext().applicationContext
        val quota = DailySimulationQuota(context)
        val day = quota.today().day
        Toast.makeText(context, "Cargando anuncio de prueba…", Toast.LENGTH_SHORT).show()
        MobileAds.initialize(context) {
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                if (!fragment.isAdded) { busy = false; onFinished(false); return@post }
                RewardedAd.load(context, context.getString(R.string.admob_rewarded_unit_id),
                    AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            busy = false
                            if (fragment.isAdded && fragment.view != null) onFinished(false)
                            Toast.makeText(context, "No se pudo cargar el anuncio. Revisa tu conexión e inténtalo nuevamente.", Toast.LENGTH_LONG).show()
                        }

                        override fun onAdLoaded(ad: RewardedAd) {
                            val activity = fragment.activity
                            if (activity == null || activity.isFinishing || activity.isDestroyed || !fragment.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
                                busy = false
                                onFinished(false)
                                return
                            }
                            var rewarded = false
                            var rewardHandled = false
                            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    busy = false
                                    if (fragment.isAdded && fragment.view != null) onFinished(rewarded)
                                    if (!rewarded) Toast.makeText(context, "El anuncio no se completó. No se descontó ni habilitó ninguna simulación.", Toast.LENGTH_LONG).show()
                                }
                                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                    busy = false
                                    if (fragment.isAdded && fragment.view != null) onFinished(false)
                                    Toast.makeText(context, "No se pudo mostrar el anuncio. Inténtalo nuevamente.", Toast.LENGTH_LONG).show()
                                }
                            }
                            ad.show(activity) {
                                if (!rewardHandled) {
                                    rewardHandled = true
                                    rewarded = quota.recordReward(day)
                                    if (rewarded) AppAnalytics.action(context, "simulation_ad_rewarded")
                                    if (!rewarded) Toast.makeText(context, "El día cambió o no se pudo guardar el avance. Vuelve a intentarlo.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    })
            }
        }
    }
}
