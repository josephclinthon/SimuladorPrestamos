package com.mocii.simuladorprestamos

import com.mocii.simuladorprestamos.ui.general.SuggestionValidation
import org.junit.Assert.*
import org.junit.Test

class SuggestionValidationTest {
    @Test fun rejectsEmptyAndWhitespaceOnlyMessages() {
        assertNotNull(SuggestionValidation.error(""))
        assertNotNull(SuggestionValidation.error("               "))
        assertNotNull(SuggestionValidation.error("  corto  "))
    }
    @Test fun allowsValidMessagesWithinLimits() {
        assertNull(SuggestionValidation.error("  Me gustaría poder exportar la comparación.  "))
        assertNull(SuggestionValidation.error("a".repeat(10)))
        assertNull(SuggestionValidation.error("a".repeat(2000)))
        assertNotNull(SuggestionValidation.error("a".repeat(2001)))
    }
}
