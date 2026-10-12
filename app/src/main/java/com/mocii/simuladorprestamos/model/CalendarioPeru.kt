import java.time.DayOfWeek
import java.time.LocalDate

object CalendarioPeru {

    // ===============================
    // 🔹 Verifica si es día hábil
    // ===============================
    fun esDiaHabil(fecha: LocalDate): Boolean {
        if (fecha.dayOfWeek == DayOfWeek.SATURDAY) return false
        if (fecha.dayOfWeek == DayOfWeek.SUNDAY) return false
        if (esFeriado(fecha)) return false
        return true
    }

    // =========================================
    // 🔹 Ajusta al siguiente día hábil
    // =========================================
    fun ajustarASiguienteDiaHabil(fecha: LocalDate): LocalDate {
        var fechaAjustada = fecha
        while (!esDiaHabil(fechaAjustada)) {
            fechaAjustada = fechaAjustada.plusDays(1)
        }
        return fechaAjustada
    }

    // =========================================
    // 🔹 Verifica si es feriado en Perú
    // =========================================
    private fun esFeriado(fecha: LocalDate): Boolean {

        val anio = fecha.year

        // ---- Feriados fijos ----
        val feriadosFijos = listOf(
            LocalDate.of(anio, 1, 1),   // Año Nuevo
            LocalDate.of(anio, 5, 1),   // Día del Trabajo
            LocalDate.of(anio, 6, 29),  // San Pedro y San Pablo
            LocalDate.of(anio, 7, 23),  // Fuerza Aérea
            LocalDate.of(anio, 7, 28),  // Fiestas Patrias
            LocalDate.of(anio, 7, 29),  // Fiestas Patrias
            LocalDate.of(anio, 8, 6),   // Batalla de Junín
            LocalDate.of(anio, 8, 30),  // Santa Rosa
            LocalDate.of(anio, 10, 8),  // Combate de Angamos
            LocalDate.of(anio, 11, 1),  // Todos los Santos
            LocalDate.of(anio, 12, 8),  // Inmaculada Concepción
            LocalDate.of(anio, 12, 9),  // Batalla de Ayacucho
            LocalDate.of(anio, 12, 25)  // Navidad
        )

        if (fecha in feriadosFijos) return true

        // ---- Semana Santa ----
        val domingoPascua = calcularDomingoPascua(anio)
        val juevesSanto = domingoPascua.minusDays(3)
        val viernesSanto = domingoPascua.minusDays(2)

        if (fecha == juevesSanto || fecha == viernesSanto) return true

        return false
    }

    // =========================================
    // 🔹 Algoritmo de Gauss para Pascua
    // =========================================
    private fun calcularDomingoPascua(anio: Int): LocalDate {

        val a = anio % 19
        val b = anio / 100
        val c = anio % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val mes = (h + l - 7 * m + 114) / 31
        val dia = ((h + l - 7 * m + 114) % 31) + 1

        return LocalDate.of(anio, mes, dia)
    }
}
