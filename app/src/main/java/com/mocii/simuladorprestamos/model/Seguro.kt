import java.math.BigDecimal

sealed class Seguro {

    // Seguro como porcentaje mensual sobre saldo
    data class Porcentaje(
        val nombre: String,
        val tasaMensual: BigDecimal
    ) : Seguro()

    // Seguro como importe fijo por cuota
    data class ImporteFijo(
        val nombre: String,
        val monto: BigDecimal
    ) : Seguro()
}
