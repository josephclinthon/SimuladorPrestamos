package com.mocii.simuladorprestamos.ui.prestamo.validation

import com.mocii.simuladorprestamos.common.utils.ValidationUtils


fun simularPrestamoForm(
    capital: String,
    tasa: String,
    numeroCuotas: String,
    fechaSolicitud: String,
    diaPago:String
): String {

    val regexFecha = "^(0[1-9]|1[0-9]|2[0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}\$"

    if (!ValidationUtils.validateNumberWithDecimal(capital) || capital.toDouble() == 0.0) {
        return "Ingrese monto a solicitar mayor a cero"
    }
    if (!ValidationUtils.validateNumberWithDecimal(tasa) || tasa.toDouble() == 0.0) {
        return "Ingrese tasa"
    }
    if (!ValidationUtils.validateNumberWithDecimal(numeroCuotas) || numeroCuotas.toInt() == 0) {
        return "Ingrese numero de cuotas"
    }
    if (!fechaSolicitud.matches(Regex(regexFecha))) {
        return "Error fecha de solitud"
    }
    if (!ValidationUtils.validateNumber(diaPago) || diaPago.toInt() == 0) {
        return "Ingrese dia de pago"
    }
    return ""
}