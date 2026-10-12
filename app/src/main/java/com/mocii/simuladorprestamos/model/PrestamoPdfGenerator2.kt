
import ResultadoPlan
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue

import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object PrestamoPdfGenerator2 {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val moneyFormat = DecimalFormat("#,##0.00")

    fun generarPdf(
        outputStream: OutputStream,
        resultado: ResultadoPlan,
        capital: BigDecimal,
        tea: BigDecimal,
        producto: String,
        currencySymbol:String
    ) {

        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf, PageSize.A4)

        val blue = DeviceRgb(33, 150, 243)
        val headerGray = DeviceRgb(240, 240, 240)
        val borderGray = DeviceRgb(220, 220, 220)

        // =========================
        // TITULO PRINCIPAL
        // =========================
        val mainTitle = Paragraph(producto)
            .setFontSize(18f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)

        document.add(mainTitle)
        document.add(Paragraph(" "))
        document.add(Paragraph(" "))

        // =========================
        // SUBTITULO
        // =========================
        val subtitle = Paragraph("Importe financiado")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)

        document.add(subtitle)

        // =========================
        // MONTO GRANDE
        // =========================
        val amount = Paragraph("${currencySymbol} ${moneyFormat.format(capital.setScale(2))}")
            .setFontSize(36f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)

        document.add(amount)
        document.add(Paragraph(" "))
        document.add(Paragraph(" "))

        // =========================
        // TABLA RESUMEN
        // =========================
        val resumenTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .setWidth(UnitValue.createPercentValue(60f))
            .setHorizontalAlignment(
                com.itextpdf.layout.properties.HorizontalAlignment.CENTER
            )

        addRow(
            resumenTable,
            "Cuota:",
            "${currencySymbol} ${moneyFormat.format(resultado.cuotas.first().totalCuota.setScale(2))}"
        )

        addRow(
            resumenTable,
            "Duración total:",
            "${resultado.cuotas.size} meses"
        )

        addRow(
            resumenTable,
            "TCEA Referencial de Operación:",
            "${resultado.tcea}%"
        )

        document.add(resumenTable)
        document.add(Paragraph(" "))
        document.add(Paragraph(" "))

        // =========================
        // LINEA AZUL + TITULO DETALLE
        // =========================
        val detalleContainer = Table(UnitValue.createPercentArray(floatArrayOf(0.01f, 0.99f)))
            .useAllAvailableWidth()

        val blueLine = Cell()
            .setBackgroundColor(blue)
            .setBorder(Border.NO_BORDER)

        val detalleTitleCell = Cell()
            .add(Paragraph("Detalle del préstamo".uppercase()).setBold())
            .setBorder(Border.NO_BORDER)
            .setPadding(8f)

        detalleContainer.addCell(blueLine)
        detalleContainer.addCell(detalleTitleCell)

        document.add(detalleContainer)
        document.add(Paragraph(" "))

        // =========================
        // TABLA DETALLE
        // =========================
        val detalleTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .useAllAvailableWidth()

        addDetailRow(detalleTable, "Producto", producto, borderGray)
        addDetailRow(detalleTable, "Importe solicitado",
            "${currencySymbol} ${moneyFormat.format(capital.setScale(2))}", borderGray)

        addDetailRow(detalleTable, "Cuota",
            "${currencySymbol} ${moneyFormat.format(resultado.cuotas.first().totalCuota.setScale(2))}", borderGray)

        addDetailRow(detalleTable, "Duración total",
            "${resultado.cuotas.size} meses", borderGray)

        addDetailRow(detalleTable, "Fecha de solicitud",
            LocalDate.now().format(formatter), borderGray)

        addDetailRow(detalleTable, "Fecha de primer pago",
            resultado.cuotas.first().fechaVencimiento.format(formatter), borderGray)

        addDetailRow(detalleTable, "Fecha de vencimiento",
            resultado.cuotas.last().fechaVencimiento.format(formatter), borderGray)

        addDetailRow(detalleTable, "Total Capital",
            "${currencySymbol} ${moneyFormat.format(resultado.totalCapital.setScale(2))}", borderGray)

        addDetailRow(detalleTable, "Total Intereses",
            "${currencySymbol} ${moneyFormat.format(resultado.totalIntereses.setScale(2))}", borderGray)

        addDetailRow(detalleTable, "Total Seguros",
            "${currencySymbol} ${moneyFormat.format(resultado.totalSeguros.setScale(2))}", borderGray)

        addDetailRow(detalleTable, "TEA",
            "${tea.setScale(6, RoundingMode.HALF_UP)} %",
            borderGray)

        document.add(detalleTable)

        // =========================
        // NUEVA PAGINA HORIZONTAL
        // =========================
        document.add(AreaBreak(PageSize.A4.rotate()))

        // =========================
        // TITULO CRONOGRAMA
        // =========================

        val detalleContainer2 = Table(UnitValue.createPercentArray(floatArrayOf(0.01f, 0.99f)))
            .useAllAvailableWidth()

        val blueLine2 = Cell()
            .setBackgroundColor(blue)
            .setBorder(Border.NO_BORDER)

        val detalleTitleCell2 = Cell()
            .add(Paragraph("Cronograma de pagos".uppercase()).setBold())
            .setBorder(Border.NO_BORDER)
            .setPadding(8f)

        detalleContainer2.addCell(blueLine2)
        detalleContainer2.addCell(detalleTitleCell2)

        document.add(detalleContainer2)
        document.add(Paragraph(" "))

        // =========================
        // TABLA CRONOGRAMA
        // =========================
        val cronogramaTable = Table(
            UnitValue.createPercentArray(
                floatArrayOf(0.7f, 1.3f, 1.2f, 1f, 1.2f, 1.1f, 1.1f)
            )
        ).useAllAvailableWidth()

        val headers = listOf(
            "MES", "VENCIMIENTO", "SALDO", "AMORTIZACIÓN",
            "INTERÉS", "SEGUROS", "CUOTA"
        )

        headers.forEach {
            cronogramaTable.addHeaderCell(
                Cell()
                    .add(Paragraph(it).setBold())
                    .setBackgroundColor(headerGray)
                    .setTextAlignment(
                        if (it == "MES" || it == "VENCIMIENTO")
                            TextAlignment.CENTER
                        else TextAlignment.RIGHT
                    )
                    .setBorderBottom(SolidBorder(borderGray, 0.5f))
                    .setPadding(8f)
            )
        }

        resultado.cuotas.forEach { cuota ->

            cronogramaTable.addCell(centerCell(cuota.numero.toString()))
            cronogramaTable.addCell(centerCell(cuota.fechaVencimiento.format(formatter)))

            cronogramaTable.addCell(rightCell("${currencySymbol} ${moneyFormat.format(cuota.saldoBase.setScale(2))}"))
            cronogramaTable.addCell(rightCell("${currencySymbol} ${moneyFormat.format(cuota.capital.setScale(2))}"))
            cronogramaTable.addCell(rightCell("${currencySymbol} ${moneyFormat.format(cuota.interes.setScale(2))}"))
            cronogramaTable.addCell(rightCell("${currencySymbol} ${moneyFormat.format(cuota.seguros.setScale(2))}"))
            cronogramaTable.addCell(rightCell("${currencySymbol} ${moneyFormat.format(cuota.totalCuota.setScale(2))}"))
        }

        document.add(cronogramaTable)

        document.close()
        println("PDF generado correctamente.")
    }

    // =========================
    // FILA RESUMEN
    // =========================
    private fun addRow(table: Table, label: String, value: String) {

        val cellLabel = Cell()
            .add(Paragraph(label))
            .setBorder(Border.NO_BORDER)

        val cellValue = Cell()
            .add(Paragraph(value))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorder(Border.NO_BORDER)

        table.addCell(cellLabel)
        table.addCell(cellValue)
    }

    // =========================
    // FILA DETALLE
    // =========================
    private fun addDetailRow(
        table: Table,
        label: String,
        value: String,
        borderColor: DeviceRgb
    ) {

        val border = SolidBorder(borderColor, 0.5f)

        val cellLabel = Cell()
            .add(Paragraph(label))
            .setBorderBottom(border)
            .setPadding(10f)

        val cellValue = Cell()
            .add(Paragraph(value))
            .setTextAlignment(TextAlignment.RIGHT)
            .setBorderBottom(border)
            .setPadding(10f)

        table.addCell(cellLabel)
        table.addCell(cellValue)
    }

    fun rightCell(text: String): Cell =
        Cell()
            .add(Paragraph(text))
            .setTextAlignment(TextAlignment.RIGHT)
            .setPadding(5f)

    fun leftCell(text: String): Cell =
        Cell()
            .add(Paragraph(text))
            .setTextAlignment(TextAlignment.LEFT)
            .setPadding(5f)

    fun centerCell(text: String): Cell =
        Cell()
            .add(Paragraph(text))
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(5f)
}



