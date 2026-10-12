package com.mocii.simuladorprestamos.ui.general

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mocii.simuladorprestamos.AppAnalytics
import com.mocii.simuladorprestamos.databinding.FragmentSuggestionBinding

class SuggestionFragment : Fragment() {
    private var _binding: FragmentSuggestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var model: SuggestionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _binding = FragmentSuggestionBinding.inflate(inflater, container, false)
        model = ViewModelProvider(this)[SuggestionViewModel::class.java]
        if (model.draft.isEmpty()) model.draft = state?.getString("draft").orEmpty()
        binding.suggestionMessage.setText(model.draft)
        binding.suggestionMessage.doAfterTextChanged {
            model.draft = it.toString()
            if (!it.isNullOrBlank() && model.status.value == SuggestionViewModel.Status.SENT) {
                model.status.value = SuggestionViewModel.Status.READY
            }
        }
        binding.sendSuggestion.setOnClickListener {
            val error = SuggestionValidation.error(model.draft)
            binding.suggestionInput.error = error
            if (error != null) return@setOnClickListener
            val network = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val connected = network.getNetworkCapabilities(network.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            if (!connected) {
                binding.suggestionStatus.text = "No hay conexión a internet. Tu texto se conserva; vuelve a intentarlo cuando tengas conexión."
                return@setOnClickListener
            }
            model.send()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        model.status.observe(viewLifecycleOwner) { status ->
            val sending = status == SuggestionViewModel.Status.SENDING
            binding.sendSuggestion.isEnabled = !sending
            binding.suggestionMessage.isEnabled = !sending
            binding.suggestionProgress.visibility = if (sending) View.VISIBLE else View.GONE
            binding.suggestionStatus.text = when (status) {
                SuggestionViewModel.Status.SENDING -> "Enviando… Si se interrumpe la conexión, espera a que vuelva para recibir la confirmación."
                SuggestionViewModel.Status.SENT -> "Gracias. Tu sugerencia se envió correctamente."
                SuggestionViewModel.Status.ERROR -> model.error
                else -> ""
            }
            if (status == SuggestionViewModel.Status.SENT) {
                binding.suggestionMessage.setText("")
                if (!model.successLogged) {
                    model.successLogged = true
                    AppAnalytics.action(requireContext(), "suggestion_sent")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppAnalytics.screen(requireContext(), "suggestions")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("draft", model.draft)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
