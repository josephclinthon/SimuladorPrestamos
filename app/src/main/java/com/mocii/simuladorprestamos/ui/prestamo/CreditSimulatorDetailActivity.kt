package com.mocii.simuladorprestamos.ui.prestamo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.mocii.simuladorprestamos.common.utils.MUtils
import com.mocii.simuladorprestamos.databinding.ActivityCreditSimulatorDetailBinding

class CreditSimulatorDetailActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCreditSimulatorDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        MUtils.setMTheme(this,1)
        //MUThemes.applyThemeColors(this)
        super.onCreate(savedInstanceState)
        binding = ActivityCreditSimulatorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return super.onSupportNavigateUp()
    }
}