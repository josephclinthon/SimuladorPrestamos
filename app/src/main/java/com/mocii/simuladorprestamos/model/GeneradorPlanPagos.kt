
import com.mocii.simuladorprestamos.common.utils.MUtils
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class GeneradorPlanPagos {

    private val contexto = MathContext(16)
    private val escala = 10
    enum class BaseAnual(val dias: Int) {
        BASE_360(360),
        BASE_365(365)
    }

    enum class TipoDias {
        PERIOD,
        REAL
    }
    enum class TipoCalendario {
        MISMO_DIA_MES,
        CADA_30_DIAS
    }
    fun calcularDiasPeriodo(
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        tipoDias: TipoDias
    ): Long {
        return when (tipoDias) {
            TipoDias.PERIOD -> 30L
            TipoDias.REAL -> ChronoUnit.DAYS.between(fechaInicio, fechaFin)
        }
    }
    /*
    fun calcularInteres(
        saldo: BigDecimal,
        tea: BigDecimal,
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        baseAnual: BaseAnual,
        tipoDias: TipoDias
    ): BigDecimal {

        val dias = calcularDiasPeriodo(fechaInicio, fechaFin, tipoDias)

        val tasaPeriodo = Math.pow(
            (BigDecimal.ONE.add(tea)).toDouble(),
            dias.toDouble() / baseAnual.dias.toDouble()
        ) - 1.0

        return saldo
            .multiply(BigDecimal(tasaPeriodo))
            .setScale(2, RoundingMode.HALF_UP)
    }*/

    fun generar(
        capital: BigDecimal,
        tasa: BigDecimal,
        numeroCuotas: Int,
        fechaDesembolsoTexto: String,
        fechaPrimerVencimientoTexto: String,
        seguros: List<Seguro>,
        baseAnual: BaseAnual,
        tipoDias: TipoDias,
        diaFijo:Boolean,
        period:Int
    ): ResultadoPlan {
        val tea = tasa.divide(BigDecimal(100))
        var acumuladoCapital = BigDecimal.ZERO
        var acumuladoInteres = BigDecimal.ZERO
        var acumuladoSeguros = BigDecimal.ZERO

        val fechaInicio = MUtils.convertirFecha(fechaDesembolsoTexto)
        val fechaPrimerVencimiento = MUtils.convertirFecha(fechaPrimerVencimientoTexto)

        val lista = mutableListOf<Cuota>()
        var saldo: BigDecimal
        var fechaAnterior: LocalDate

        // =============================
        // 1️⃣ Cuota inicial estimada
        // =============================
        val tasaMensualAprox = tea.divide(BigDecimal("12"), 12, RoundingMode.HALF_UP)

        val unoMasI = BigDecimal.ONE.add(tasaMensualAprox)
        val potencia = unoMasI.pow(numeroCuotas)

        var cuotaBase = capital.multiply(
            tasaMensualAprox.multiply(potencia)
                .divide(potencia.subtract(BigDecimal.ONE), 12, RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP)

        // =============================
        // 2️⃣ Búsqueda binaria cuota real
        // =============================

        var cuotaMin = cuotaBase.multiply(BigDecimal("0.90"))
        var cuotaMax = cuotaBase.multiply(BigDecimal("1.10"))
        var nuevaCuotaBase = cuotaBase

        var iteraciones = 0
        var saldoFinal: BigDecimal

        //
        val periodInc = period/30

        do {

            lista.clear()
            saldo = capital
            fechaAnterior = fechaInicio

            acumuladoCapital = BigDecimal.ZERO
            acumuladoInteres = BigDecimal.ZERO
            acumuladoSeguros = BigDecimal.ZERO

            for (i in 1..numeroCuotas) {

                var fechaCuota = if(i == 1){fechaPrimerVencimiento}else{fechaPrimerVencimiento.plusMonths(((i-1)*periodInc).toLong())}
                if (!diaFijo&&tipoDias == TipoDias.REAL) {
                    fechaCuota = CalendarioPeru.ajustarASiguienteDiaHabil(fechaCuota)
                }

                val dias = when (tipoDias) {
                    TipoDias.PERIOD -> period.toLong()
                    TipoDias.REAL ->
                        ChronoUnit.DAYS.between(fechaAnterior, fechaCuota)
                }

                val factorTiempo = BigDecimal(dias)
                    .divide(BigDecimal(30), 12, RoundingMode.HALF_UP)

                val tasaPeriodo = Math.pow(
                    (BigDecimal.ONE.add(tea)).toDouble(),
                    dias.toDouble() / baseAnual.dias.toDouble()
                ) - 1.0

                val tasaPeriodoBD = BigDecimal(tasaPeriodo)

                val saldoBase = saldo

                val interes = saldoBase
                    .multiply(tasaPeriodoBD)
                    .setScale(2, RoundingMode.HALF_UP)

                val segurosPorcentaje = seguros
                    .filterIsInstance<Seguro.Porcentaje>()
                    .map {
                        saldoBase
                            .multiply(it.tasaMensual) // 0.000714
                            .multiply(factorTiempo)
                    }
                    .fold(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP)

                val totalSegurosFijos = seguros
                    .filterIsInstance<Seguro.ImporteFijo>()
                    .map { it.monto }
                    .fold(BigDecimal.ZERO, BigDecimal::add)

                val totalSeguros = segurosPorcentaje
                    .add(totalSegurosFijos)
                    .setScale(2, RoundingMode.HALF_UP)

                var capitalCuota = nuevaCuotaBase
                    .subtract(interes)
                    .subtract(totalSeguros)
                    .setScale(2, RoundingMode.HALF_UP)

                saldo = saldo.subtract(capitalCuota)
                if(i == numeroCuotas){
                    capitalCuota += saldo
                }

                acumuladoCapital += capitalCuota
                acumuladoInteres += interes
                acumuladoSeguros += totalSeguros
                val totalCuota = capitalCuota+interes+totalSeguros
                lista.add(
                    Cuota(
                        numero = i,
                        fechaInicio = fechaAnterior,
                        fechaVencimiento = fechaCuota,
                        saldoBase = saldoBase.setScale(2, RoundingMode.HALF_UP),
                        capital = capitalCuota,
                        interes = interes,
                        seguros = totalSeguros,
                        totalCuota = totalCuota.setScale(2, RoundingMode.HALF_UP)
                    )
                )

                fechaAnterior = fechaCuota
            }

            saldoFinal = saldo.setScale(2, RoundingMode.HALF_UP)

            if (saldoFinal > BigDecimal.ZERO) {
                cuotaMin = nuevaCuotaBase
            } else {
                cuotaMax = nuevaCuotaBase
            }

            nuevaCuotaBase = cuotaMin.add(cuotaMax)
                .divide(BigDecimal("2"), 8, RoundingMode.HALF_UP)

            iteraciones++

        } while (saldoFinal.abs() > BigDecimal("0.01") && iteraciones < 50)

        // =============================
        // 3️⃣ Calcular TCEA
        // =============================

        val tceaCalculada = calcularTCEAReferencial(tea)

        return ResultadoPlan(
            cuotas = lista,
            totalCapital = acumuladoCapital.setScale(2, RoundingMode.HALF_UP),
            totalIntereses = acumuladoInteres.setScale(2, RoundingMode.HALF_UP),
            totalSeguros = acumuladoSeguros.setScale(2, RoundingMode.HALF_UP),
            tcea = tceaCalculada.setScale(6, RoundingMode.HALF_UP)
        )
    }

    // ===============================
    // Cálculo TIR mensual → TCEA anual
    // ===============================

    private fun calcularTCEAReferencial(teaDecimal: BigDecimal): BigDecimal {

        val uno = BigDecimal.ONE

        // Convertir TEA a TEM usando double (como lo hacen muchos bancos)
        val tem = Math.pow(
            uno.add(teaDecimal).toDouble(),
            1.0 / 12.0
        ) - 1.0

        // Volver a anualizar
        val tcea = Math.pow(
            1.0 + tem,
            12.0
        ) - 1.0

        return BigDecimal.valueOf(tcea)
            .multiply(BigDecimal(100))
            .setScale(6, RoundingMode.HALF_UP)
    }


    data class ResultadoAmortizacion(
        val nuevoPlan: ResultadoPlan,
        val interesAhorrado: BigDecimal
    )
    enum class TipoAmortizacion {
        REDUCCION_CUOTA,
        REDUCCION_PLAZO
    }

}
