package com.mocii.simuladorprestamos.ui.general

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mocii.simuladorprestamos.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVersion()
        setupLinks()
    }

    // =========================================================
    // VERSIÓN
    // =========================================================

    private fun setupVersion() {
        binding.tvVersion.text = "Versión ${getAppVersionName()}"
    }

    private fun getAppVersionName(): String {
        return try {
            val packageInfo = requireContext()
                .packageManager
                .getPackageInfo(requireContext().packageName, 0)

            packageInfo.versionName ?: "1.0.0"

        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }

    // =========================================================
    // ENLACES
    // =========================================================

    private fun setupLinks() {

        binding.llYoutube.setOnClickListener {
            openUrl("https://www.youtube.com/@MociiApps")
        }

        binding.llFacebook.setOnClickListener {
            openUrl("https://www.facebook.com/mociiapps")
        }

        binding.llTiktok.setOnClickListener {
            openUrl("https://www.tiktok.com/@mociiapps")
        }

        binding.llContact.setOnClickListener {
            sendEmail()
        }

        binding.llRate.setOnClickListener {
            openPlayStoreListing()
        }

        binding.llShare.setOnClickListener {
            shareApp()
        }
    }

    // =========================================================
    // ABRIR URL
    // =========================================================

    private fun openUrl(url: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )

        startActivity(intent)
    }

    // =========================================================
    // CORREO
    // =========================================================

    private fun sendEmail() {

        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:mocii.apps@gmail.com")
        ).apply {
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Sugerencia - Simulador de Préstamos"
            )
        }

        startActivity(
            Intent.createChooser(
                intent,
                "Enviar correo"
            )
        )
    }

    // =========================================================
    // CALIFICAR EN PLAY STORE
    // =========================================================

    private fun openPlayStoreListing() {

        val packageName = requireContext().packageName

        try {

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )

            startActivity(intent)

        } catch (e: ActivityNotFoundException) {

            openUrl(
                "https://play.google.com/store/apps/details?id=$packageName"
            )
        }
    }

    // =========================================================
    // COMPARTIR APP
    // =========================================================

    private fun shareApp() {

        val packageName = requireContext().packageName

        val shareText =
            "¡Descarga Simulador de Préstamos!\n\n" +
                    "https://play.google.com/store/apps/details?id=$packageName"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(
            Intent.createChooser(
                intent,
                "Compartir aplicación"
            )
        )
    }

    // =========================================================
    // LIMPIEZA
    // =========================================================

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}