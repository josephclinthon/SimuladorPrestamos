package com.mocii.simuladorprestamos.model

data class Basic(
    val id:Long,
    val desc:String,
    val state:Char
) {
    var subDesc:String = ""
    var ai01:Int = 0
    var ai02:Int = 0
    val ai03:Int = 0
    var as01:String = ""
    var as02:String = ""
    var as03:String = ""
    var ad01:Double = 0.0
    val ad02:Double = 0.0
    val ad03:Double = 0.0


}