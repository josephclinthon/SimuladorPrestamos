package com.mocii.simuladorprestamos.common.utils

import java.util.Currency
import java.util.Locale

class CurrencyUtils {

    companion object {

        fun getCurrencyByDevice(): String {
            return try {
                val locale = Locale.getDefault()
                val currency = Currency.getInstance(locale)
                currency.currencyCode // Ej: USD, PEN, EUR
            } catch (e: Exception) {
                "USD" // fallback por defecto
            }
        }

        fun getCurrencySymbolByDevice(): String {
            return try {
                val locale = Locale.getDefault()
                val currency = Currency.getInstance(locale)
                currency.getSymbol(locale) // Ej: $, S/, €
            } catch (e: Exception) {
                "$"
            }
        }

        fun getCurrencyNameByDevice(): String {
            return try {
                val locale = Locale.getDefault()
                val currency = Currency.getInstance(locale)
                currency.displayName // Ej: Sol peruano, US Dollar
            } catch (e: Exception) {
                "US Dollar"
            }
        }
    }
}