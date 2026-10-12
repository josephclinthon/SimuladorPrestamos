package com.mocii.simuladorprestamos

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/** Only action names are recorded; never loan inputs or suggestion contents. */
object AppAnalytics {
    fun action(context: Context, name: String) {
        FirebaseAnalytics.getInstance(context.applicationContext).logEvent(name, null)
    }

    fun screen(context: Context, name: String) {
        FirebaseAnalytics.getInstance(context.applicationContext).logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, name)
            })
    }
}
