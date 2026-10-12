import java.math.BigDecimal

data class ResultadoPlan(
    val cuotas: List<Cuota>,
    val totalCapital: BigDecimal,
    val totalIntereses: BigDecimal,
    val totalSeguros: BigDecimal,
    val tcea: BigDecimal
)