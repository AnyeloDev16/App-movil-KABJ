package pe.kabj.app_movil_kabj.util.extensions

import androidx.core.text.isDigitsOnly
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Valida que el campo de texto dentro del TextInputLayout no esté vacío.
 * Si está vacío, muestra un mensaje de error en el TextInputLayout.
 *
 * @param errorMessage El mensaje que se mostrará si el campo está vacío.
 * @return true si el campo tiene texto; false si está vacío.
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

/**
 * Valida que el campo de texto dentro del TextInputLayout contenga solo números.
 * Si el campo está vacío o contiene caracteres no numéricos, muestra un mensaje de error.
 *
 * @param errorMessage El mensaje que se mostrará si el campo no es un número válido.
 * @return true si el campo contiene solo números; false en caso contrario.
 */
fun TextInputLayout.validateIsNumber(errorMessage: String): Boolean {
    val input = this.editText as? TextInputEditText
    val text = input?.text?.toString()?.trim()
    return if (text.isNullOrEmpty() || !text.isDigitsOnly()) {
        this.error = errorMessage
        false
    } else {
        this.error = null
        true
    }
}
