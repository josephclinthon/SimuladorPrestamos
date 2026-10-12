package com.mocii.simuladorprestamos.common.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.mocii.simuladorprestamos.R

object MUThemes {
    var mThemeCurrent = 0
    var mThemeFullScreenCurrent = 0
    var mThemeColorCurrent = 0

    var mColorPrimary = 0
    var mColorOnPrimary = 0
    var mColorSecondary = 0
    var mColorOnSecondary = 0
    var colorPrimaryContainer = 0
    var colorOnPrimaryContainer = 0

    const val THEME_COLOR_DEFAULT= 5

    fun getMTheme(context: Context): MTheme {
        return MTheme(mColorPrimary,mColorOnPrimary,mColorSecondary,mColorOnSecondary)
    }
    fun setMTheme(activity: Activity, fullScreen:Int){
        if (mThemeColorCurrent > 0) {
            if (fullScreen == 0) {
                activity.setTheme(mThemeCurrent)
            }
            if (fullScreen == 1) {
                activity.setTheme(mThemeFullScreenCurrent)
            }
        }
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        mColorPrimary = typedValue.data
        activity.theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
        mColorOnPrimary= typedValue.data
        activity.theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        mColorSecondary = typedValue.data
        activity.theme.resolveAttribute(R.attr.colorOnSecondary, typedValue, true)
        mColorOnSecondary = typedValue.data
        //
        activity.theme.resolveAttribute(R.attr.colorPrimaryContainer, typedValue, true)
        colorPrimaryContainer = typedValue.data
        activity.theme.resolveAttribute(R.attr.colorOnPrimaryContainer, typedValue, true)
        colorOnPrimaryContainer = typedValue.data
    }
    fun switchMTheme(activity: Activity, color:Int, isChecked:Boolean){
        if (isChecked && mThemeColorCurrent != color) {
            if(getMThemeColor(color) > 0) {
                MCurrent.dataManager?.createThemeColor(color)
                val intent = activity.intent
                activity.finish()
                activity.startActivity(intent)
            }
        }
    }
    fun getMThemeColor(color:Int):Int{
        var findColor:Boolean = true
        Log.d("MOCII","mThemeColorCurrent: $mThemeColorCurrent")
        Log.d("MOCII","color: $color")
        if (mThemeColorCurrent != color){
            when (color) {
                1 -> {
                    mThemeCurrent = R.style.AppTheme01
                    mThemeFullScreenCurrent = R.style.AppTheme01FullScreen
                }
                2 -> {
                    mThemeCurrent = R.style.AppTheme02
                    mThemeFullScreenCurrent = R.style.AppTheme02FullScreen
                }
                3 -> {
                    mThemeCurrent = R.style.AppTheme03
                    mThemeFullScreenCurrent = R.style.AppTheme03FullScreen
                }
                4 -> {
                    mThemeCurrent = R.style.AppTheme04
                    mThemeFullScreenCurrent = R.style.AppTheme04FullScreen
                }
                5 -> {
                    mThemeCurrent = R.style.AppTheme05
                    mThemeFullScreenCurrent = R.style.AppTheme05FullScreen
                }
                6 -> {
                    mThemeCurrent = R.style.AppTheme06
                    mThemeFullScreenCurrent = R.style.AppTheme06FullScreen
                }
                7 -> {
                    mThemeCurrent = R.style.AppTheme07
                    mThemeFullScreenCurrent = R.style.AppTheme07FullScreen
                }
                8 -> {
                    mThemeCurrent = R.style.AppTheme08
                    mThemeFullScreenCurrent = R.style.AppTheme08FullScreen
                }
                9 -> {
                    mThemeCurrent = R.style.AppTheme09
                    mThemeFullScreenCurrent = R.style.AppTheme09FullScreen
                }
                10 -> {
                    mThemeCurrent = R.style.AppTheme10
                    mThemeFullScreenCurrent = R.style.AppTheme10FullScreen
                }
                11 -> {
                    mThemeCurrent = R.style.AppTheme11
                    mThemeFullScreenCurrent = R.style.AppTheme11FullScreen
                }
                else -> {
                    findColor = false
                    mThemeColorCurrent = THEME_COLOR_DEFAULT
                    mThemeCurrent = R.style.AppTheme05
                    mThemeFullScreenCurrent = R.style.AppTheme05FullScreen
                }
            }

        }
        if(findColor){
            mThemeColorCurrent = color
        }
        return if (findColor) {
            color
        }else{
            0
        }
    }

}