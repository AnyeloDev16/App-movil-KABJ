package pe.kabj.app_movil_kabj.util.extensions

import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.getTrimmedText(): String = this.text?.toString()?.trim() ?: ""