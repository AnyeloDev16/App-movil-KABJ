package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.presentation.enums.ValidationState

class AssignWorkOrderViewModel : ViewModel() {

    private val _selectedFileUri: MutableLiveData<Uri?> = MutableLiveData()
    val selectedFileUri: LiveData<Uri?> get() = _selectedFileUri

    private val _selectedFileName: MutableLiveData<String> = MutableLiveData()
    val selectedFileName: LiveData<String> get() = _selectedFileName

    private val _fileSizeFormatted: MutableLiveData<String> = MutableLiveData()
    val fileSizeFormatted: LiveData<String> get() = _fileSizeFormatted

    private val _progressPercent: MutableLiveData<Int> = MutableLiveData(0)
    val progressPercent: LiveData<Int> get() = _progressPercent

    private val _validationState = MutableLiveData(ValidationState.NONE)
    val validationState: LiveData<ValidationState> get() = _validationState

    private val _isVisibleFileInfo: MutableLiveData<Boolean> = MutableLiveData()
    val isVisibleFileInfo: LiveData<Boolean> get() = _isVisibleFileInfo

    fun setSelectedFile(uri: Uri, context: Context) {
        _selectedFileUri.value = uri
        _selectedFileName.value = extractFileName(context, uri)
        _fileSizeFormatted.value = getFileSizeText(context, uri)
        _isVisibleFileInfo.value = true
        startValidationSimulation()
    }

    fun clearFileSelection() {
        _isVisibleFileInfo.value = false
        _selectedFileUri.value = null
        _selectedFileName.value = ""
        _fileSizeFormatted.value = ""
        _progressPercent.value = 0
        _validationState.value = ValidationState.NONE
    }

    private fun startValidationSimulation() {
        _validationState.value = ValidationState.VALIDATING
        _progressPercent.value = 0

        val handler = Handler(Looper.getMainLooper())

        for (i in 1..10) {
            handler.postDelayed({
                _progressPercent.value = i * 10
            }, i * 300L)
        }

        handler.postDelayed({
            val valid = true // Puedes poner lógica real aquí
            _validationState.value = if (valid) ValidationState.SUCCESS else ValidationState.ERROR
        }, 3000)
    }

    private fun extractFileName(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex) ?: "archivo.xlsx"
        } ?: "archivo.xlsx"
    }

    private fun getFileSizeText(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE)
        return cursor?.use {
            it.moveToFirst()
            val sizeInBytes = if (sizeIndex != null && sizeIndex >= 0) it.getLong(sizeIndex) else -1L
            formatFileSize(sizeInBytes)
        } ?: "Tamaño desconocido"
    }

    private fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 0 -> "Tamaño desconocido"
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> "${(sizeInBytes / 1024.0).format(1)} kB"
            else -> "${(sizeInBytes / (1024.0 * 1024)).format(1)} MB"
        }
    }

    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)

}