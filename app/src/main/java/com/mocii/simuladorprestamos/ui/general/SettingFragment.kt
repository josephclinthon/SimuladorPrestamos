package com.mocii.simuladorprestamos.ui.general

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.mocii.simuladorprestamos.DataManager
import com.mocii.simuladorprestamos.R
import com.mocii.simuladorprestamos.common.utils.MConst
import com.mocii.simuladorprestamos.common.utils.MUThemes
import com.mocii.simuladorprestamos.common.utils.MUtils
import com.mocii.simuladorprestamos.databinding.FragmentSettingBinding
import com.mocii.simuladorprestamos.model.Current

class SettingFragment : Fragment(R.layout.fragment_setting){
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    var dataManager: DataManager? = null
    private val rewardedAds by lazy { com.mocii.simuladorprestamos.RewardedSimulationAds(this) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        dataManager = DataManager(this.requireContext())
        binding.tietSettingCurrencyForm.setText(dataManager?.getCurrencySign()?:MConst.DEFAULT_COIN)
        binding.cbSettingViewModeDarkForm.isChecked = dataManager?.getFlagModeDark()?:true
        binding.cbSettingProfessionalVersionForm.isChecked = dataManager?.getProfessionalVersion()?:true
        val language = dataManager?.getLanguage()?: MConst.LANGUAGE_DEFAULT

        if (language==MConst.LANGUAGE_ENGLISH){
            binding.rbSettingLanguageEnglish.isChecked = true
        }else{
            binding.rbSettingLanguageSpanish.isChecked = true
        }
        binding.buttonSettingSaveObjective.setOnClickListener{
            dataManager?.createCurrencySign(""+binding.tietSettingCurrencyForm.text)
            dataManager?.createProfessionalVersion(binding.cbSettingProfessionalVersionForm.isChecked)
            val intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }
        binding.cbSettingViewModeDarkForm.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            dataManager?.createFlagModeDark(binding.cbSettingViewModeDarkForm.isChecked)
            Current.modeDark = binding.cbSettingViewModeDarkForm.isChecked
        }
        binding.rgSettingLanguage.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_setting_language_spanish -> {
                    MUtils.setAppLocale(requireContext(),MConst.LANGUAGE_DEFAULT)
                    dataManager?.createLanguage(MConst.LANGUAGE_DEFAULT)
                }
                R.id.rb_setting_language_english -> {
                    MUtils.setAppLocale(requireContext(),MConst.LANGUAGE_ENGLISH)
                    dataManager?.createLanguage(MConst.LANGUAGE_ENGLISH)
                }
            }
            val intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }
        //COLOR
        for (i in 1..11) {
            val radioButton = when (i) {
                1 -> binding.rbSettingColor01
                2 -> binding.rbSettingColor02
                3 -> binding.rbSettingColor03
                4 -> binding.rbSettingColor04
                5 -> binding.rbSettingColor05
                6 -> binding.rbSettingColor06
                7 -> binding.rbSettingColor07
                8 -> binding.rbSettingColor08
                9 -> binding.rbSettingColor09
                10 -> binding.rbSettingColor10
                11 -> binding.rbSettingColor11
                else -> null
            }
            radioButton?.isChecked = (MUThemes.mThemeColorCurrent == i)
        }
        binding.rbSettingColor01.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 1, binding.rbSettingColor01.isChecked)
        }

        binding.rbSettingColor02.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 2, binding.rbSettingColor02.isChecked)
        }

        binding.rbSettingColor03.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 3, binding.rbSettingColor03.isChecked)
        }

        binding.rbSettingColor04.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 4, binding.rbSettingColor04.isChecked)
        }

        binding.rbSettingColor05.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 5, binding.rbSettingColor05.isChecked)
        }

        binding.rbSettingColor06.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 6, binding.rbSettingColor06.isChecked)
        }

        binding.rbSettingColor07.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 7, binding.rbSettingColor07.isChecked)
        }

        binding.rbSettingColor08.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 8, binding.rbSettingColor08.isChecked)
        }

        binding.rbSettingColor09.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 9, binding.rbSettingColor09.isChecked)
        }

        binding.rbSettingColor10.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 10, binding.rbSettingColor10.isChecked)
        }

        binding.rbSettingColor11.setOnClickListener {
            MUThemes.switchMTheme(requireActivity(), 11, binding.rbSettingColor11.isChecked)
        }
        binding.bDailyAd.setOnClickListener { rewardedAds.offer { refreshQuota() } }
        binding.bSuggestions.setOnClickListener {
            androidx.navigation.fragment.NavHostFragment.findNavController(this).navigate(R.id.SuggestionFragment)
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        refreshQuota()
        com.mocii.simuladorprestamos.AppAnalytics.screen(requireContext(), "settings")
    }

    private fun refreshQuota() {
        if (_binding == null) return
        val state = com.mocii.simuladorprestamos.DailySimulationQuota(requireContext()).today()
        binding.tvDailyQuota.text = state.description() + "\nEl contador se reinicia a medianoche según la fecha del dispositivo."
        binding.bDailyAd.isEnabled = !state.unlimited
        binding.bDailyAd.text = if (state.unlimited) "Ilimitadas habilitadas hoy" else "Ver anuncio de prueba (${state.ads}/3)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}