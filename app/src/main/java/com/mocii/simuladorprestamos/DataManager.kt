package com.mocii.simuladorprestamos

import android.content.Context
import android.content.SharedPreferences
import com.mocii.simuladorprestamos.common.utils.MConst
import com.mocii.simuladorprestamos.common.utils.MUThemes

class DataManager(context: Context) {
    var pref: SharedPreferences? = null
    var prefEdit: SharedPreferences.Editor? = null

    private val IS_LOGIN:String = "IsLoggedIn"
    private val KEY_TOKEN:String = "token"
    private val KEY_OBJECTIVE:String = "objective"
    private val KEY_VIEW_ALL_SUB_aCCOUNTS:String = "view_all_sub_accounts"
    private val KEY_CURRENY_SIGN:String = "currency_sign"
    private val KEY_FLAG_MODE_DARK:String = "mode_dark"
    private val KEY_FLAG_INIT:String = "init"
    private val KEY_LANGUAGE:String = "language"
    private val KEY_THEME_COLOR:String = "theme_color"
    private val KEY_ACCOUNT:String = "account"
    private val KEY_PROFESSIONAL_VERSION:String = "professional_version"

    fun createProfessionalVersion(token:Boolean){
        prefEdit?.putBoolean(KEY_PROFESSIONAL_VERSION,token)
        prefEdit?.commit()
    }
    fun createLoginSession(token:String){
        prefEdit?.putString(KEY_TOKEN,token)
        prefEdit?.commit()
    }
    fun createCurrencySign(value:String){
        prefEdit?.putString(KEY_CURRENY_SIGN,value)
        prefEdit?.commit()
    }
    fun createFlagModeDark(value:Boolean){
        prefEdit?.putBoolean(KEY_FLAG_MODE_DARK,value)
        prefEdit?.commit()
    }
    fun createObjective(value:String){
        prefEdit?.putString(KEY_OBJECTIVE,value)
        prefEdit?.commit()
    }
    fun createFlagViewSubAccounts(value:Boolean){
        prefEdit?.putBoolean(KEY_VIEW_ALL_SUB_aCCOUNTS,value)
        prefEdit?.commit()
    }
    fun createFlagInit(value:Boolean){
        prefEdit?.putBoolean(KEY_FLAG_INIT,value)
        prefEdit?.commit()
    }
    fun createLanguage(value:String){
        prefEdit?.putString(KEY_LANGUAGE,value)
        prefEdit?.commit()
    }
    fun createCountAds(key:String,value:Int){
        prefEdit?.putInt(key,value)
        prefEdit?.commit()
    }
    fun createThemeColor(value:Int){
        prefEdit?.putInt(KEY_THEME_COLOR,value)
        prefEdit?.commit()
    }
    fun createAccount(value:Int){
        prefEdit?.putInt(KEY_ACCOUNT,value)
        prefEdit?.commit()
    }
    /*fun getUsersDetailFromSession():HashMap<String,String>(){
        val userData:HashMap<String,String> = HashMap<String,String>()
        return userData
    }*/
    fun logoutUserFromSession(){
        prefEdit?.clear()
        prefEdit?.commit()
    }
    fun getProfessionalVersion():Boolean{
        val token = pref?.getBoolean(KEY_PROFESSIONAL_VERSION,false)?:false
        return token
    }
    fun getUserDetailFromSession():String?{
        val token = pref?.getString(KEY_TOKEN,null)
        return token
    }

    fun getObjective():String{
        val token = pref?.getString(KEY_OBJECTIVE,null)
        return token?:"GASTO"
    }
    fun getCurrencySign():String{
        val token = pref?.getString(KEY_CURRENY_SIGN,null)
        return token?:"S/"
    }
    fun getFlagModeDark():Boolean{
        val token = pref?.getBoolean(KEY_FLAG_MODE_DARK,true)
        return token?:true
    }
    fun getFlagViewSubAccounts():Boolean{
        val flagViewAllSubAccounts = pref?.getBoolean(KEY_VIEW_ALL_SUB_aCCOUNTS,false)
        return flagViewAllSubAccounts?:false
    }
    fun getFlagInit():Boolean{
        val flagInit= pref?.getBoolean(KEY_FLAG_INIT,false)
        return flagInit?:false
    }
    fun getLanguage():String?{
        val token = pref?.getString(KEY_LANGUAGE,null)
        return token?:null
    }
    fun getCountAds(key:String):Int{
        val token = pref?.getInt(key,0)
        return token?:0
    }
    fun getThemeColor():Int{
        val token = pref?.getInt(KEY_THEME_COLOR, MUThemes.THEME_COLOR_DEFAULT)
        return token?:MUThemes.THEME_COLOR_DEFAULT
    }
    fun getAccount():Int{
        val token = pref?.getInt(KEY_ACCOUNT, MConst.DEFAULT_ACCOUNT)
        return token?:MConst.DEFAULT_ACCOUNT
    }
    init {
        pref = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE)
        prefEdit = pref?.edit()
    }

}