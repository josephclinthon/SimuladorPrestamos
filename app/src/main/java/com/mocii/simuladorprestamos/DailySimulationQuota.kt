package com.mocii.simuladorprestamos

import android.content.Context
import java.time.LocalDate

data class DailyQuotaState(val day: String, val simulations: Int = 0, val ads: Int = 0) {
    val unlimited get() = ads >= 3
    val freeRemaining get() = (3 - simulations).coerceAtLeast(0)
    val remaining get() = freeRemaining
    val canSimulate get() = unlimited || remaining > 0
    fun forDay(today: String) = if (day == today) this else DailyQuotaState(today)
    fun afterSimulation(rewarded: Boolean = false): DailyQuotaState {
        check(canSimulate || (rewarded && ads > 0))
        return copy(simulations = simulations + 1)
    }
    fun afterReward() = copy(ads = (ads + 1).coerceAtMost(3))
    fun description() = if (unlimited) "Full activo por hoy · Simulaciones ilimitadas\nAnuncios completados: 3/3"
        else "Simulaciones gratuitas restantes: $freeRemaining/3\nSimulaciones disponibles: $remaining\nAnuncios completados: $ads/3\nCompleta 3 anuncios para simular sin límite hoy."
}

class DailySimulationQuota(context: Context) {
    private val preferences = context.getSharedPreferences("daily_simulation_quota", Context.MODE_PRIVATE)
    fun today(): DailyQuotaState = DailyQuotaState(
        preferences.getString("day", "").orEmpty(),
        preferences.getInt("simulations", 0), preferences.getInt("ads", 0)
    ).forDay(LocalDate.now().toString())

    fun recordSimulation(rewardedDay: String? = null): Boolean = today().let { state ->
        val rewarded = rewardedDay == state.day && state.ads > 0
        (state.canSimulate || rewarded) && write(state.afterSimulation(rewarded))
    }

    fun recordReward(adDay: String): Boolean {
        val state = today()
        return state.day == adDay && write(state.afterReward())
    }

    private fun write(state: DailyQuotaState) = preferences.edit()
        .putString("day", state.day).putInt("simulations", state.simulations)
        .putInt("ads", state.ads).commit()
}
