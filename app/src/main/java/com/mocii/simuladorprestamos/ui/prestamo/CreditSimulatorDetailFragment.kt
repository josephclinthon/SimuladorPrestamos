package com.mocii.simuladorprestamos.ui.prestamo

import Cuota
import GeneradorPlanPagos
import ResultadoPlan
import Seguro
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.mocii.simuladorprestamos.R
import com.mocii.simuladorprestamos.common.utils.MUThemes
import com.mocii.simuladorprestamos.common.utils.MUtils
import com.mocii.simuladorprestamos.databinding.FragmentCreditSimulatorDetailBinding
import com.mocii.simuladorprestamos.model.Current
import com.mocii.simuladorprestamos.ui.prestamo.master.CuotasAdapter
import org.koin.android.ext.android.inject
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.text.DecimalFormat

class CreditSimulatorDetailFragment : Fragment(R.layout.fragment_credit_simulator_detail), CuotasAdapter.OnItemClickListener {

    private val cuotasAdapter: CuotasAdapter by inject()

    private var _binding: FragmentCreditSimulatorDetailBinding? = null

    private val binding get() = _binding!!
    //val args: BuyItemsFragmentArgs by navArgs()

    var menuTemp: Menu? = null
    var menuInflaterTemp: MenuInflater? = null
    var menuChange = -1
    var producto:String = "Prestamo"
    var tasa:BigDecimal = BigDecimal("12")
    var capital:BigDecimal = BigDecimal("1000")
    var resultado:ResultadoPlan = GeneradorPlanPagos().generar(
        capital = capital,
        tasa = tasa,
        numeroCuotas = 10,
        fechaDesembolsoTexto = "01/01/2000",
        fechaPrimerVencimientoTexto = "01/02/2000",
        seguros = listOf(),
        baseAnual = GeneradorPlanPagos.BaseAnual.BASE_360,
        tipoDias = GeneradorPlanPagos.TipoDias.REAL,
        diaFijo = false,
        period = 30
    )
    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding.rvFragmentItemBuyList.adapter = cuotasAdapter
        _binding = FragmentCreditSimulatorDetailBinding.inflate(inflater, container, false)

        val extras = requireActivity().intent.extras
        var subAccount:Int = 0
        var capitalStr = "0"
        var tasaStr = "0"
        var numeroCuotasStr = "0"
        var fechaSolicitud = "01/02/2000"
        var diaPago = "0"
        var porcentajeSeguro01 = "0"
        var annualBaseStr = "360"
        var fixedDay = true
        var periodStr = "30"
        var paymentDayTypeStr = "01"
        if (extras!=null){
            capitalStr = requireActivity().intent.getStringExtra("capital")?:"0"
            numeroCuotasStr = requireActivity().intent.getStringExtra("numeroCuotas")?:"0"
            tasaStr = requireActivity().intent.getStringExtra("tasa")?:"0"
            fechaSolicitud = requireActivity().intent.getStringExtra("fechaSolicitud")?:"01/02/2000"
            diaPago = requireActivity().intent.getStringExtra("diaPago")?:"0"
            porcentajeSeguro01 = requireActivity().intent.getStringExtra("porcentajeSeguro01")?:"0"
            annualBaseStr = requireActivity().intent.getStringExtra("annualBase")?:"360"
            periodStr = requireActivity().intent.getStringExtra("period")?:"30"
            paymentDayTypeStr = requireActivity().intent.getStringExtra("paymentDayType")?:"01"
        }
        var tipoDias = GeneradorPlanPagos.TipoDias.REAL
        val paymentDayType = paymentDayTypeStr.substring(0,2)
        if(paymentDayType.equals("01")){
            fixedDay = true
        }
        if(paymentDayType.equals("02")){
            fixedDay = false
        }
        if(paymentDayType.equals("03")){
            fixedDay = false
            tipoDias = GeneradorPlanPagos.TipoDias.PERIOD
        }
        val period = periodStr.toInt()
        val generador = GeneradorPlanPagos()
        //Seguro.Porcentaje("Desgravamen", BigDecimal("0.000714"))
        val seguros = arrayListOf<Seguro.Porcentaje>(
        )
        if(!porcentajeSeguro01.equals("0") && !porcentajeSeguro01.equals("")){
            seguros.add(Seguro.Porcentaje("Seguro 01", BigDecimal(porcentajeSeguro01).divide(BigDecimal("100"))))
        }
        tasa = BigDecimal(tasaStr)
        val capital = BigDecimal(capitalStr)
        val fechaPrimerVencimiento = MUtils.siguienteMesConDia(fechaSolicitud,diaPago.toInt())
        var annualBase = GeneradorPlanPagos.BaseAnual.BASE_360
        if(!annualBaseStr.equals("360")){
            annualBase = GeneradorPlanPagos.BaseAnual.BASE_365
        }
        resultado = generador.generar(
            capital = capital,
            tasa = tasa?:BigDecimal("0"),
            numeroCuotas = numeroCuotasStr.toInt(),
            fechaDesembolsoTexto = fechaSolicitud,
            fechaPrimerVencimientoTexto = fechaPrimerVencimiento,
            seguros = seguros,
            baseAnual = annualBase,
            tipoDias = tipoDias,
            diaFijo = fixedDay,
            period = period
        )

        Log.d("TAG", "HOLA"+resultado.cuotas)
        cuotasAdapter.clear()
        cuotasAdapter.fillList(resultado.cuotas)
        cuotasAdapter.notifyDataSetChanged()
        val colorPrimary = MUThemes.getMTheme(this.requireContext()).colorPrimary
        cuotasAdapter.colorPrimary = colorPrimary
        val moneyFormat = DecimalFormat("#,##0.00")
        binding.tvSimularPrestamoCapital.text = Current.coin + " "+moneyFormat.format(resultado.totalCapital)
        binding.tvSimularPrestamoInteresTotal.text = Current.coin + " "+moneyFormat.format(resultado.totalIntereses)
        binding.tvSimularPrestamoSegurosTotal.text = Current.coin + " "+moneyFormat.format(resultado.totalSeguros)
        binding.tvSimularPrestamoTasa.text = tasa.toString()+" %"
        binding.tvSimularPrestamoCuotaTotal.text = Current.coin + " "+resultado.cuotas.first().totalCuota
        binding.tvSimularPrestamoFechaSolicitud.text = fechaSolicitud
        binding.fabSimularBackButton.setOnClickListener { view ->
            requireActivity().finish()
        }
        binding.fabSimularShareButton.setOnClickListener { view ->
            actionShare(resultado, capital, tasa)
        }
        binding.fabSimularDownloadButton.setOnClickListener { view ->
            actionDownload(resultado, capital, tasa)
        }
        //val toolbar: Toolbar = findViewById(R.id.toolbar)
        //requireActivity().setSupportActionBar(binding.toolbar)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        com.mocii.simuladorprestamos.BannerAds.attach(this, binding.bannerContainer)
        // Accede al NavController y establece la configuración del AppBar
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        // Configura la barra de herramientas (Toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // Configura la ActionBar con NavController y la configuración del AppBar
        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(navController, appBarConfiguration)

        // Muestra el botón de navegación en la ActionBar
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            //activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_left2)
            //activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.llBuyItemActionsCenter.visibility = View.GONE
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // Obtener la altura total del CollapsingToolbarLayout
            val totalScrollRange = appBarLayout.totalScrollRange

            // Obtener la fracción de desplazamiento actual
            val offsetFraction = Math.abs(verticalOffset.toFloat()) / totalScrollRange.toFloat()

            // Configurar la visibilidad del NavigationIcon basándose en la fracción de desplazamiento
            val activity = requireActivity() as AppCompatActivity
            val actionBar = activity.supportActionBar
            Log.d("TAG","actionBar:"+actionBar)
            if (offsetFraction >= 0.8f) {
                actionBar?.setDisplayHomeAsUpEnabled(true)

                actionBar?.title = "Simulación de Prestamo"
                if (menuChange != 0) {
                    /*if (buy?.state == MConst.STATE_LOCAL) {
                        menuTemp?.clear()
                        menuInflaterTemp?.inflate(R.menu.buy_top_nav_menu, menuTemp)
                    } else {
                        menuInflaterTemp?.inflate(R.menu.top_nav_share, menuTemp)
                    }*/
                    menuInflaterTemp?.inflate(R.menu.menu_prestamo_detalle, menuTemp)
                    binding.llBuyItemActionsCenter.visibility = View.GONE
                    menuChange = 0
                }
            } else {
                actionBar?.setDisplayHomeAsUpEnabled(false)
                if (menuChange != 1) {
                    actionBar?.title = ""
                    binding.llBuyItemActionsCenter.visibility = View.VISIBLE
                    menuTemp?.clear()
                    //menuInflaterTemp?.inflate(R.menu.top_nav_share, menuTemp)
                    menuChange = 1
                }
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
    }
    private fun setupRecyclerView() {
        cuotasAdapter.setOnItemClickListener(this)
        binding.rvFragmentItemBuyList.adapter = cuotasAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menuTemp = menu
        this.menuInflaterTemp = inflater
        /*if (buy?.state == MConst.STATE_LOCAL){
            inflater.inflate(R.menu.buy_top_nav_menu, menu)
        }else{
            inflater.inflate(R.menu.top_nav_share, menu)
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_simular_share -> {
                actionShare(resultado, capital, tasa)
                true
            }
            R.id.action_simular_download -> {
                //buyItemViewModel.deleteBuy(buy!!,requireActivity())
                actionDownload(resultado, capital, tasa)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(buyItem: Cuota, container: View) {

    }
    fun actionShare(resultado: ResultadoPlan,capital:BigDecimal,tasa:BigDecimal){
        //val file = File(context?.getExternalFilesDir(null), "prestamo.pdf")
        val file = File(context?.cacheDir, "prestamo.pdf")
        val currencySymbol = Current.coin
        FileOutputStream(file).use { outputStream ->
            PrestamoPdfGenerator2.generarPdf(
                outputStream,
                resultado,
                capital,
                tasa,
                producto,
                currencySymbol
            )
        }

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(intent)
    }
    fun actionDownload(resultado: ResultadoPlan, capital: BigDecimal, tasa: BigDecimal) {

        val currencySymbol = Current.coin

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadAndroid10Plus(resultado, capital, tasa, currencySymbol)
        } else {
            downloadLegacy(resultado, capital, tasa, currencySymbol)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadAndroid10Plus(
        resultado: ResultadoPlan,
        capital: BigDecimal,
        tasa: BigDecimal,
        currencySymbol: String
    ) {
        val resolver = requireContext().contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "prestamo.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val uri = resolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                PrestamoPdfGenerator2.generarPdf(
                    outputStream, resultado, capital, tasa, producto, currencySymbol
                )
            }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)

            Toast.makeText(requireContext(), "PDF guardado en Descargas", Toast.LENGTH_LONG).show()

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(it, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(openIntent)
        }
    }

    private fun downloadLegacy(
        resultado: ResultadoPlan,
        capital: BigDecimal,
        tasa: BigDecimal,
        currencySymbol: String
    ) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )

        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val file = File(downloadsDir, "prestamo.pdf")

        FileOutputStream(file).use { outputStream ->
            PrestamoPdfGenerator2.generarPdf(
                outputStream, resultado, capital, tasa, producto, currencySymbol
            )
        }

        MediaScannerConnection.scanFile(
            requireContext(),
            arrayOf(file.absolutePath),
            arrayOf("application/pdf"),
            null
        )

        Toast.makeText(requireContext(), "PDF guardado en Descargas", Toast.LENGTH_LONG).show()

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(openIntent)
    }
}
