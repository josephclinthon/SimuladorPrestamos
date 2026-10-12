package com.mocii.simuladorprestamos.ui.prestamo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.google.android.material.datepicker.MaterialDatePicker
import com.mocii.simuladorprestamos.DataManager
import com.mocii.simuladorprestamos.R
import com.mocii.simuladorprestamos.common.utils.MUtils
import com.mocii.simuladorprestamos.databinding.FragmentCreditSimulatorFormBinding
import com.mocii.simuladorprestamos.ui.prestamo.validation.simularPrestamoForm
import java.io.Console
import java.util.Locale

class CreditSimulatorFormFragment : Fragment() {

    private var _binding: FragmentCreditSimulatorFormBinding? = null

    private val binding get() = _binding!!
    val dias = (1..31).map { it.toString() }
    val annualDays = arrayListOf("360","365")
    val periods = arrayListOf("30","60","90","180","360")
    var paymentDayTypes: List<String> = emptyList()

    var dataManager: DataManager? = null
    var isProfessionalVersion = false
    private var editingId: String? = null
    private var simulationAdLoading = false
    private val rewardedAds by lazy { com.mocii.simuladorprestamos.RewardedSimulationAds(this) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditSimulatorFormBinding.inflate(inflater, container, false)
        val context = requireContext()

        paymentDayTypes = arrayListOf(
            "01 " + context.getString(R.string.title_fixed_day),
            "02 " + context.getString(R.string.title_calendar_day),
            "03 " + context.getString(R.string.title_business_day)
        )
        dataManager = DataManager(this.requireContext())
        isProfessionalVersion = dataManager?.getProfessionalVersion()?:false
        //
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            dias
        )
        val adapterAnnualDays = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            annualDays
        )
        val adapterPeriods = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            periods
        )
        val adapterPaymentDayTypes = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            paymentDayTypes
        )
        //
        binding.actvSimularDiaPagoForm.setAdapter(adapter)
        binding.actvSimularAnnualBaseForm.setAdapter(adapterAnnualDays)
        binding.actvSimularPeriodForm.setAdapter(adapterPeriods)
        binding.actvSimularPaymentDayTypeForm.setAdapter(adapterPaymentDayTypes)
        //
        binding.tietSimularFechaSolicitudForm.setText(MUtils.getFechaActual())
        binding.tietSimularCapitalForm.setText("1000")
        binding.tietSimularCuotasForm.setText("12")
        binding.tietSimularTasaForm.setText("12")
        binding.actvSimularDiaPagoForm.setText(MUtils.getFechaActual().substring(0,2),false)
        binding.actvSimularPeriodForm.setText("30",false)
        binding.actvSimularPaymentDayTypeForm.setText("01 "+context.getString(R.string.title_fixed_day),false)
        //binding.cbSettingFixedDayForm.isChecked = true
        binding.actvSimularAnnualBaseForm.setText("360",false)
        //binding.cbSettingFixedDayForm.isEnabled = false
        //binding.cbSettingFixedDayForm.isVisible = false
        if(isProfessionalVersion){
            //binding.actvSimularAnnualBaseForm.isVisible = true
            binding.tilSimularAnnualBaseForm.isVisible = true
            binding.tilSimularPeriodForm.isVisible = true
            binding.tilSimularPaymentDayTypeForm.isVisible = true
        }else{
            //binding.actvSimularAnnualBaseForm.isVisible = false
            binding.tilSimularAnnualBaseForm.isVisible = false
            binding.tilSimularPeriodForm.isVisible = false
            binding.tilSimularPaymentDayTypeForm.isVisible = false
        }
        //
        binding.tilCalculateStartDateForm.setStartIconOnClickListener{
            val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setTitleText("Fecha de Solicitud")
                .build()
            datePicker.show(childFragmentManager, "DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener {
                binding.tietSimularFechaSolicitudForm.setText(MUtils.getStrDatePicker(it))
                binding.actvSimularDiaPagoForm.setText(MUtils.getStrDatePicker(it).substring(0,2))
            }
        }
        editingId = arguments?.getString("simulationId")
        val saved = SimulationHistory(context).all().find { it.id == editingId }
        saved?.inputs?.let { values ->
            binding.tietSimularCapitalForm.setText(values["capital"])
            binding.tietSimularTasaForm.setText(values["tasa"])
            binding.tietSimularCuotasForm.setText(values["numeroCuotas"])
            binding.tietSimularFechaSolicitudForm.setText(values["fechaSolicitud"])
            binding.tietSimularSeguro01Form.setText(values["porcentajeSeguro01"])
            binding.actvSimularDiaPagoForm.setText(values["diaPago"], false)
            binding.actvSimularAnnualBaseForm.setText(values["annualBase"], false)
            binding.actvSimularPeriodForm.setText(values["period"], false)
            binding.actvSimularPaymentDayTypeForm.setText(paymentDayTypes.first { it.take(2) == values.getValue("paymentDayType").take(2) }, false)
            binding.bSimular.text = "Guardar cambios y simular"
        }
        binding.bHistory.setOnClickListener {
            androidx.navigation.fragment.NavHostFragment.findNavController(this).navigate(R.id.SimulationHistoryFragment)
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        com.mocii.simuladorprestamos.BannerAds.attach(this, binding.bannerContainer)
        binding.bSimular.setOnClickListener {
            val capital = binding.tietSimularCapitalForm.text.toString()
            val tasa = binding.tietSimularTasaForm.text.toString()
            val numeroCuotas = binding.tietSimularCuotasForm.text.toString()
            val fechaSolicitud = binding.tietSimularFechaSolicitudForm.text.toString()
            val diaPago = binding.actvSimularDiaPagoForm.text.toString()
            val porcentajeSeguro01 = binding.tietSimularSeguro01Form.text.toString()
            val annualBase = binding.actvSimularAnnualBaseForm.text.toString()
            val fixedDay = true//binding.cbSettingFixedDayForm.isChecked

            val period = binding.actvSimularPeriodForm.text.toString()
            val paymentDayType = binding.actvSimularPaymentDayTypeForm.text.toString()
            var errorDesc = try { simularPrestamoForm(
                capital,
                tasa,
                numeroCuotas,
                fechaSolicitud,
                diaPago
            ) } catch (_: Exception) { "Revisa el número de cuotas y los datos ingresados" }
            if (errorDesc.equals("") ){
                val country = MUtils.obtenerPais(requireContext())//Locale.getDefault().country
                Log.d("MOCII","country "+country)
                if (paymentDayType.substring(0,2).equals("03")&&!country.equals("PE")){
                    errorDesc = "No disponible en su pais."
                }
            }
            if (errorDesc.isEmpty() && (porcentajeSeguro01.ifBlank { "0" }.toBigDecimalOrNull()?.signum() ?: -1) < 0) {
                errorDesc = "Ingresa un porcentaje de seguro válido"
            }
            if (errorDesc.equals("") ){
                //if(dias.contains(diaPago)) {
                    val intent = Intent(this.context, CreditSimulatorDetailActivity::class.java)
                    intent.putExtra("capital", capital)
                    intent.putExtra("tasa", tasa)
                    intent.putExtra("numeroCuotas", numeroCuotas)
                    intent.putExtra("fechaSolicitud", fechaSolicitud)
                    intent.putExtra("diaPago", diaPago)
                    intent.putExtra("porcentajeSeguro01", porcentajeSeguro01)
                    intent.putExtra("annualBase", annualBase)
                    intent.putExtra("fixedDay", fixedDay)
                    intent.putExtra("period", period)
                    intent.putExtra("paymentDayType", paymentDayType)
                    val inputs = listOf("capital", "tasa", "numeroCuotas", "fechaSolicitud", "diaPago", "porcentajeSeguro01", "annualBase", "period", "paymentDayType")
                        .associateWith { intent.getStringExtra(it).orEmpty() }
                    val record = SavedSimulation(id = editingId ?: java.util.UUID.randomUUID().toString(), inputs = inputs)
                    try {
                        record.calculate()
                        val state = com.mocii.simuladorprestamos.DailySimulationQuota(requireContext()).today()
                        if (!state.canSimulate) {
                            simulationAdLoading = true
                            refreshQuota()
                            rewardedAds.watch { completed ->
                                simulationAdLoading = false
                                if (_binding != null) {
                                    refreshQuota()
                                    if (completed) completeSimulation(record, intent, state.day)
                                }
                            }
                        } else completeSimulation(record, intent)
                    } catch (_: Exception) {
                        MUtils.alert(requireContext(), "No se pudo simular. Revisa la fecha y los datos ingresados")
                    }
                //}else{
                //    MUtils.alert(requireContext(),"Dia de pago incorrecto.")
                //}
            }else{
                MUtils.alert(requireContext(),errorDesc)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshQuota()
        com.mocii.simuladorprestamos.AppAnalytics.screen(requireContext(), "simulator")
    }

    private fun completeSimulation(record: SavedSimulation, intent: Intent, rewardedDay: String? = null) {
        val quota = com.mocii.simuladorprestamos.DailySimulationQuota(requireContext())
        val state = quota.today()
        if (!state.canSimulate && (rewardedDay != state.day || state.ads == 0)) return
        if (!SimulationHistory(requireContext()).save(record)) {
            MUtils.alert(requireContext(), "No se pudo guardar la simulación")
            return
        }
        if (!quota.recordSimulation(rewardedDay)) {
            MUtils.alert(requireContext(), "No se pudo registrar el uso diario. Inténtalo nuevamente")
            return
        }
        if (editingId != null) {
            arguments?.remove("simulationId")
            editingId = null
        }
        refreshQuota()
        com.mocii.simuladorprestamos.AppAnalytics.action(requireContext(), "simulation_completed")
        startActivity(intent)
    }

    private fun refreshQuota() {
        if (_binding == null) return
        val state = com.mocii.simuladorprestamos.DailySimulationQuota(requireContext()).today()
        binding.tvDailyQuota.text = state.description()
        binding.bSimular.isEnabled = !simulationAdLoading
        binding.bSimular.text = when {
            simulationAdLoading -> "Cargando anuncio…"
            !state.canSimulate -> if (editingId != null) "Ver anuncio, guardar y simular" else "Ver anuncio y simular"
            editingId != null -> "Guardar cambios y simular"
            else -> getString(R.string.title_simulate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
