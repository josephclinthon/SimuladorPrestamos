package com.mocii.simuladorprestamos.ui.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mocii.simuladorprestamos.AppAnalytics
import com.mocii.simuladorprestamos.DailySimulationQuota
import com.mocii.simuladorprestamos.RewardedSimulationAds
import com.mocii.simuladorprestamos.databinding.FragmentFullAccessBinding

class FullAccessFragment : Fragment() {
    private var _binding: FragmentFullAccessBinding? = null
    private val binding get() = _binding!!
    private val ads by lazy { RewardedSimulationAds(this) }
    private var loading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentFullAccessBinding.inflate(inflater, container, false)
        binding.watchFullAd.setOnClickListener {
            loading = true
            refresh()
            ads.watch {
                loading = false
                if (_binding != null) refresh()
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refresh()
        AppAnalytics.screen(requireContext(), "full_access")
    }

    private fun refresh() {
        val state = DailySimulationQuota(requireContext()).today()
        binding.fullProgress.progress = state.ads
        binding.fullStatus.text = if (state.unlimited) "Full activo por hoy"
            else "Anuncios completados: ${state.ads}/3"
        binding.fullRemaining.text = if (state.unlimited) "Ya puedes hacer todas las simulaciones que quieras hoy."
            else "Te quedan ${3 - state.ads} anuncios para activar simulaciones ilimitadas hoy.\nSimulaciones gratuitas restantes: ${state.freeRemaining}/3."
        binding.watchFullAd.text = when {
            state.unlimited -> "Full activado"
            loading -> "Cargando anuncio…"
            else -> "Ver anuncio ${state.ads + 1} de 3"
        }
        binding.watchFullAd.isEnabled = !loading && !state.unlimited
        binding.fullLoading.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
