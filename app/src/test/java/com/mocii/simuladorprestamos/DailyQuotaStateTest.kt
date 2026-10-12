package com.mocii.simuladorprestamos

import org.junit.Assert.*
import org.junit.Test

class DailyQuotaStateTest {
    @Test fun threeFreeSimulationsThenRewardIsRequired() {
        var state = DailyQuotaState("2026-10-12")
        repeat(3) {
            assertTrue(state.canSimulate)
            state = state.afterSimulation()
        }
        assertEquals(0, state.freeRemaining)
        assertFalse(state.canSimulate)
        state = state.afterReward()
        assertEquals(0, state.remaining)
        assertFalse(state.canSimulate)
        state = state.afterSimulation(rewarded = true)
        assertFalse(state.canSimulate)
        state = state.afterReward().afterSimulation(rewarded = true)
        assertFalse(state.canSimulate)
        state = state.afterReward()
        repeat(100) { state = state.afterSimulation() }
        assertTrue(state.unlimited)
        assertEquals(3, state.ads)
    }

    @Test fun nextDayResetsBothCountersAndUnlimitedAccess() {
        val yesterday = DailyQuotaState("2026-10-12", 90, 3)
        val today = yesterday.forDay("2026-10-13")
        assertEquals(3, today.freeRemaining)
        assertEquals(0, today.ads)
        assertFalse(today.unlimited)
        assertEquals(yesterday, yesterday.forDay("2026-10-12"))
    }

    @Test fun watchingAdsInAdvanceUnlocksSameDay() {
        var state = DailyQuotaState("2026-10-12")
        repeat(3) { state = state.afterReward() }
        assertTrue(state.unlimited)
        assertEquals(3, state.freeRemaining)
        assertEquals(3, state.afterReward().ads)
    }

    @Test(expected = IllegalStateException::class)
    fun blockedSimulationCannotConsumeAnotherUse() {
        DailyQuotaState("2026-10-12", 3, 0).afterSimulation()
    }

    @Test(expected = IllegalStateException::class)
    fun partialFullProgressDoesNotBypassAdAfterFreeLimit() {
        DailyQuotaState("2026-10-12", 3, 2).afterSimulation()
    }

    @Test(expected = IllegalStateException::class)
    fun missingRewardCannotUnlockSimulation() {
        DailyQuotaState("2026-10-12", 3, 0).afterSimulation(rewarded = true)
    }
}
