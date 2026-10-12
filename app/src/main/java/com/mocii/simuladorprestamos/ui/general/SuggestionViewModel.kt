package com.mocii.simuladorprestamos.ui.general

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object SuggestionValidation {
    fun error(message: String): String? = when {
        message.trim().length < 10 -> "Escribe una sugerencia de al menos 10 caracteres."
        message.trim().length > 2000 -> "La sugerencia puede tener hasta 2000 caracteres."
        else -> null
    }
}

class SuggestionViewModel : ViewModel() {
    enum class Status { READY, SENDING, SENT, ERROR }
    val status = MutableLiveData(Status.READY)
    var draft = ""
    var successLogged = false
    var error = ""
        private set

    fun send() {
        if (status.value == Status.SENDING) return
        val message = draft.trim()
        SuggestionValidation.error(message)?.let { fail(it); return }
        status.value = Status.SENDING
        successLogged = false
        save(message)
    }

    private fun save(message: String) {
        FirebaseFirestore.getInstance().collection("suggestions").document().set(
            mapOf("message" to message, "createdAt" to FieldValue.serverTimestamp())
        ).addOnSuccessListener {
            draft = ""
            status.value = Status.SENT
        }.addOnFailureListener {
            fail("No se pudo enviar la sugerencia. Tu texto se conserva para que puedas intentarlo nuevamente.")
        }
    }

    private fun fail(message: String) {
        error = message
        status.value = Status.ERROR
    }
}
