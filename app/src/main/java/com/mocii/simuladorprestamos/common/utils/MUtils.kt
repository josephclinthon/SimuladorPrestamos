package com.mocii.simuladorprestamos.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.TimeZone

object MUtils {
    val formatoFecha = "dd/MM/yyyy"
    @SuppressLint("ServiceCast")
    fun obtenerPais(context: Context): String {

        // 1. Locale
        val locale = Locale.getDefault().country
        if (locale.isNotEmpty()) return locale.uppercase()

        // 2. SIM
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val sim = tm.simCountryIso
        if (!sim.isNullOrEmpty()) return sim.uppercase()

        // 3. Red
        val network = tm.networkCountryIso
        if (!network.isNullOrEmpty()) return network.uppercase()

        // 4. Fallback
        return "PE"
    }
    fun alert(context:Context,mensaje:String){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    }

    fun siguienteMesConDia(fechaTexto: String, diaDeseado: Int): String {

        val formatter = DateTimeFormatter.ofPattern(formatoFecha)

        // Parsear fecha de entrada
        val fecha = LocalDate.parse(fechaTexto, formatter)

        // Ir al siguiente mes
        val siguienteMes = fecha.plusMonths(1)

        // Obtener último día válido del mes
        val ultimoDiaDelMes = siguienteMes.lengthOfMonth()

        // Ajustar si el día no existe
        val diaFinal = if (diaDeseado > ultimoDiaDelMes) {
            ultimoDiaDelMes
        } else {
            diaDeseado
        }

        val fechaFinal = siguienteMes.withDayOfMonth(diaFinal)

        // Retornar en dd/MM/yyyy
        return fechaFinal.format(formatter)
    }


    fun getDatePicker(date:String,time:String):Long{
        val sdf = SimpleDateFormat(formatoFecha + " HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val fullTime = date + " " + time
        return sdf.parse(fullTime).time
    }
    fun getStrDatePickerToOrder(date:Long):String{
        val sdf = SimpleDateFormat(formatoFecha, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }
    fun getStrDatePicker(date:Long):String{
        val sdf = SimpleDateFormat(formatoFecha, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }
    fun convertirFecha(fechaTexto: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(formatoFecha)

        return try {
            LocalDate.parse(fechaTexto, formatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Formato de fecha inválido. Use "+formatoFecha)
        }
    }
    fun getFechaActual():String{
        val formatter = DateTimeFormatter.ofPattern(formatoFecha)
        val fechaActual = LocalDate.now().format(formatter)
        return fechaActual
    }
    fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //context.createConfigurationContext(config)
        //} else {
        //@Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        //}
    }
    fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    fun setMTheme(activity: Activity, fullScreen:Int){
        MUThemes.setMTheme(activity,fullScreen)
    }
}