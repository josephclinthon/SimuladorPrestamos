package com.mocii.simuladorprestamos.ui.prestamo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mocii.simuladorprestamos.R
import com.mocii.simuladorprestamos.model.Current
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.Date

class SimulationHistoryFragment : Fragment() {
    private val selected = linkedSetOf<String>()
    private lateinit var content: LinearLayout
    private lateinit var compare: MaterialButton
    private lateinit var bannerContainer: android.widget.FrameLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        content = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
        state?.getStringArrayList("selected")?.let { selected.addAll(it) }
        bannerContainer = android.widget.FrameLayout(requireContext()).apply {
            minimumHeight = (50 * resources.displayMetrics.density).toInt()
        }
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            addView(bannerContainer, LinearLayout.LayoutParams(-1, -2))
            addView(ScrollView(requireContext()).apply { addView(content) }, LinearLayout.LayoutParams(-1, 0, 1f))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.mocii.simuladorprestamos.BannerAds.attach(this, bannerContainer)
    }

    override fun onResume() {
        super.onResume()
        render()
        com.mocii.simuladorprestamos.AppAnalytics.screen(requireContext(), "simulation_history")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList("selected", ArrayList(selected))
        super.onSaveInstanceState(outState)
    }

    private fun render() {
        val history = SimulationHistory(requireContext())
        val records = history.all()
        selected.retainAll(records.map { it.id }.toSet())
        content.removeAllViews()
        content.addView(TextView(requireContext()).apply {
            text = "Selecciona dos simulaciones para comparar. Los datos se guardan en este dispositivo."
            textSize = 16f
        })
        compare = button("Comparar (${selected.size}/2)") {
            val chosen = selected.mapNotNull { id -> records.find { it.id == id } }
            if (chosen.size == 2) showComparison(chosen[0], chosen[1])
        }
        compare.isEnabled = selected.size == 2
        if (records.isEmpty()) content.addView(TextView(requireContext()).apply {
            text = "Todavía no hay simulaciones. Simula un préstamo desde el inicio para guardarlo."
            textSize = 18f
        })
        records.forEach { record ->
            content.addView(CheckBox(requireContext()).apply {
                text = "${money(record.inputs.getValue("capital").toBigDecimal())} · ${record.inputs["numeroCuotas"]} cuotas · TEA ${record.inputs["tasa"]}%\n${DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(record.updatedAt))}"
                isChecked = record.id in selected
                setOnCheckedChangeListener { _, checked ->
                    if (checked && selected.size == 2) {
                        isChecked = false
                        Toast.makeText(context, "Selecciona solo dos simulaciones", Toast.LENGTH_SHORT).show()
                    } else {
                        if (checked) selected.add(record.id) else selected.remove(record.id)
                        compare.text = "Comparar (${selected.size}/2)"
                        compare.isEnabled = selected.size == 2
                    }
                }
            })
            button("Abrir simulación") {
                com.mocii.simuladorprestamos.AppAnalytics.action(requireContext(), "simulation_reopened")
                startActivity(record.intent(requireContext()))
            }
            button("Editar") {
                findNavController().navigate(R.id.FirstFragment, Bundle().apply { putString("simulationId", record.id) })
            }
            button("Eliminar") {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Eliminar simulación")
                    .setMessage("¿Eliminar esta simulación del historial?")
                    .setNegativeButton("Cancelar", null).setPositiveButton("Eliminar") { _, _ ->
                        if (history.delete(record.id)) {
                            com.mocii.simuladorprestamos.AppAnalytics.action(requireContext(), "simulation_deleted")
                            selected.remove(record.id); render()
                        }
                        else Toast.makeText(context, "No se pudo eliminar", Toast.LENGTH_LONG).show()
                    }.show()
            }
        }
    }

    private fun button(label: String, action: () -> Unit): MaterialButton = MaterialButton(requireContext()).apply {
        text = label
        setOnClickListener { action() }
        content.addView(this)
    }

    private fun money(value: java.math.BigDecimal) = "${Current.coin} ${DecimalFormat("#,##0.00").format(value)}"

    private fun showComparison(a: SavedSimulation, b: SavedSimulation) {
        try {
            com.mocii.simuladorprestamos.AppAnalytics.action(requireContext(), "simulation_compared")
            val first = a.calculate()
            val second = b.calculate()
            val table = android.widget.TableLayout(requireContext()).apply { isStretchAllColumns = true }
            fun row(vararg values: String) {
                table.addView(android.widget.TableRow(requireContext()).apply {
                    values.forEach { value -> addView(TextView(context).apply {
                        text = value; setPadding(8, 16, 8, 16); textSize = 14f
                    }) }
                })
            }
            row("Concepto", "A", "B", "B − A")
            row("Cuotas", a.inputs.getValue("numeroCuotas"), b.inputs.getValue("numeroCuotas"), "")
            row("TEA", "${a.inputs["tasa"]}%", "${b.inputs["tasa"]}%", "")
            listOf(
                Triple("Capital", first.totalCapital, second.totalCapital),
                Triple("Intereses", first.totalIntereses, second.totalIntereses),
                Triple("Seguros", first.totalSeguros, second.totalSeguros),
                Triple("Total a pagar", first.totalCapital + first.totalIntereses + first.totalSeguros,
                    second.totalCapital + second.totalIntereses + second.totalSeguros)
            ).forEach { (label, x, y) -> row(label, money(x), money(y), money(y - x)) }
            val horizontal = android.widget.HorizontalScrollView(requireContext()).apply { addView(table) }
            MaterialAlertDialogBuilder(requireContext()).setTitle("Comparación de simulaciones")
                .setView(ScrollView(requireContext()).apply { addView(horizontal) })
                .setPositiveButton("Cerrar", null).show()
        } catch (exception: Exception) {
            Toast.makeText(context, "No se pudo calcular la comparación. Revisa los datos guardados.", Toast.LENGTH_LONG).show()
        }
    }
}
