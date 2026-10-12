package com.mocii.simuladorprestamos.common.utils

object ValidationUtils {
    fun validateDate(value:String):Boolean{
        val regex = "^(0[1-9]|1[0-9]|2[0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}\$"
        return value.matches(Regex(regex))
    }
    fun validateTime(date:String):Boolean{
        val regexPrice = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\$"
        return date.matches(Regex(regexPrice))
    }
    fun validateNumber(value:String):Boolean{
        val regex = "^\\d+\$"
        return value.matches(Regex(regex))
    }
    fun validateNumberWithDecimal(value:String):Boolean{
        val regex = "^-?\\d+(\\.\\d{1,2})?\$"
        return value.matches(Regex(regex))
    }
    fun validateString(value:String,maxLength:Int):Boolean{
        val regex = """^.{1,$maxLength}$"""
        return value.matches(Regex(regex))

    }
}