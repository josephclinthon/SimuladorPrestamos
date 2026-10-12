import java.math.BigDecimal
import java.time.LocalDate

data class Cuota(
    val numero: Int,
    val fechaInicio: LocalDate,
    val fechaVencimiento: LocalDate,
    val saldoBase: BigDecimal,
    val capital: BigDecimal,
    val interes: BigDecimal,
    val seguros: BigDecimal,
    val totalCuota: BigDecimal
)