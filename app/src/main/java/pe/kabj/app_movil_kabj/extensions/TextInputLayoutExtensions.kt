package pe.kabj.app_movil_kabj.extensions

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Valida que el campo de texto dentro del TextInputLayout no esté vacío.
 * Muestra un error si está vacío.
 */
fun TextInputLayout.validateNotEmpty(errorMessage: String): Boolean {
    val input = this.editText as? TextInputEditText
    val text = input?.text?.toString()?.trim()
    return if (text.isNullOrEmpty()) {
        this.error = errorMessage
        false
    } else {
        this.error = null
        true
    }
}