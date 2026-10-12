package com.mocii.simuladorprestamos.common.utils

import android.app.Activity
import android.os.Build
import android.os.Debug
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.mocii.simuladorprestamos.DataManager
import com.mocii.simuladorprestamos.model.Current

object MCurrent {
    var dataManager: DataManager? = null
    fun mInitApp(activity: Activity){
        dataManager = DataManager(activity)

        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity.resources.configuration.locales[0] // Para API 24 (Nougat) o superior
        } else {
            activity.resources.configuration.locale // Para API inferior a 24
        }
        val language = dataManager?.getLanguage()
        if (language == null) {
            if (currentLocale.language == MConst.LANGUAGE_DEFAULT) {
                dataManager?.createLanguage(currentLocale.language)
            }else{
                dataManager?.createLanguage(MConst.LANGUAGE_ENGLISH)
            }
        }
        MUtils.setAppLocale(
            activity,
            dataManager?.getLanguage()?:MConst.LANGUAGE_DEFAULT
        )
        val color = dataManager?.getThemeColor()?: MUThemes.THEME_COLOR_DEFAULT
        Log.d("MOCII","COLOR: $color")
        MUThemes.getMThemeColor(color)
        if (dataManager?.getFlagModeDark()?:MConst.MODE_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        Current.modeDark = dataManager?.getFlagModeDark()?:MConst.MODE_DARK

    }
    fun mInitMainActivity(activity: Activity){
        val coin = dataManager?.getCurrencySign()
        if (coin == null){
            dataManager?.createCurrencySign(CurrencyUtils.getCurrencySymbolByDevice())
        }
        Current.coin = dataManager?.getCurrencySign()?: MConst.DEFAULT_COIN
        Current.language = dataManager?.getLanguage()?:MConst.LANGUAGE_DEFAULT
        //MUThemes.mThemeColorCurrent = dataManager?.getThemeColor()?:MUThemes.THEME_COLOR_DEFAULT
        Current.modeDark = dataManager?.getFlagModeDark()?:MConst.MODE_DARK
    }
}