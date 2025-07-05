package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.data.api.WorkOrderApiService
import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient
import pe.kabj.app_movil_kabj.data.dto.ErrorResponse
import pe.kabj.app_movil_kabj.data.dto.OperationResult
import pe.kabj.app_movil_kabj.data.dto.workorder.BatchResponse
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderRegisterRequest
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.model.enum.Permission
import pe.kabj.app_movil_kabj.presentation.enums.ValidationState
import pe.kabj.app_movil_kabj.util.FileUtils
import pe.kabj.app_movil_kabj.util.UploadFileData

class RegisterWorkOrderViewModel(
    private val context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val retrofitClient = RetrofitClient.getInstance(context)
    private val workOrderApiService: WorkOrderApiService = retrofitClient.createAuthService(WorkOrderApiService::class.java)

    private lateinit var listWorkOrderRegister: List<WorkOrderRegisterRequest>

    private val _btnProcessFileEnabled = MutableLiveData<Boolean>()
    val btnProcessFileEnabled: LiveData<Boolean> get() = _btnProcessFileEnabled

    // ===== Card view ========

    private val _fileName = MutableLiveData<String>()
    val fileName: LiveData<String> get() = _fileName

    private val _fileSize = MutableLiveData<String>()
    val fileSize: LiveData<String> get() = _fileSize

    private val _isValidating = MutableLiveData<ValidationState>()
    val isValidating: LiveData<ValidationState> get() = _isValidating

    // ==== message ====

    private val _messageResult = MutableLiveData<OperationResult>()
    val messageResult: LiveData<OperationResult> get() = _messageResult

    fun uploadUri(uri: Uri) {

        _btnProcessFileEnabled.postValue(false)
        listWorkOrderRegister = emptyList()

        if (!sessionManager.hasPermission(Permission.MOBILE_WORK_ORDER_CREATE)) {
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para registrar órdenes de trabajo."))
            return
        }

        val file: UploadFileData = FileUtils.createUploadFileData(context, uri)

        _fileName.postValue(file.fileName)
        _fileSize.postValue(file.fileSizeFormatted)
        _isValidating.postValue(ValidationState.VALIDATING)

        workOrderApiService.excelValidate(file.multipartBody)
            .enqueue(object : retrofit2.Callback<List<WorkOrderRegisterRequest>> {

                override fun onResponse(
                    call: retrofit2.Call<List<WorkOrderRegisterRequest>>,
                    response: retrofit2.Response<List<WorkOrderRegisterRequest>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        _isValidating.postValue(ValidationState.SUCCESS)
                        listWorkOrderRegister = response.body()!!
                        _btnProcessFileEnabled.postValue(true)
                        _messageResult.postValue(OperationResult.Success(
                            "Validación exitosa!",
                            "El archivo fue validado correctamente y los datos se procesaron sin problemas.")
                        )
                    } else {
                        _isValidating.postValue(ValidationState.ERROR)
                        val errorDto = ErrorResponse.extractErrorDto(response)
                        _messageResult.postValue(OperationResult.Error("Registro Fallido", errorDto.message))
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<List<WorkOrderRegisterRequest>>,
                    t: Throwable
                ) {
                    _isValidating.postValue(ValidationState.ERROR)
                    val message = when {
                        t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                            "No se pudo conectar al servidor. Verifica tu conexión a internet."

                        t.message?.contains("timeout", ignoreCase = true) == true ->
                            "La solicitud tardó demasiado. Intenta nuevamente."

                        else ->
                            "Error de red: ${t.localizedMessage ?: "desconocido"}"
                    }

                    _messageResult.postValue(
                        OperationResult.Error("Error de Red", message))
                }
            })
    }

    fun processSelectedFile(){

        _btnProcessFileEnabled.postValue(false)

        if (!sessionManager.hasPermission(Permission.MOBILE_WORK_ORDER_CREATE)) {
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para registrar órdenes de trabajo."))
            return
        }

        if(listWorkOrderRegister.isEmpty()){
            _messageResult.postValue(OperationResult.Error("Error Inesperado", "No se cuentan con registros de Ordenes de Trabajo a Registrar."))
            return
        }

        workOrderApiService.saveAll(listWorkOrderRegister)
            .enqueue(object : retrofit2.Callback<BatchResponse> {

                override fun onResponse(
                    call: retrofit2.Call<BatchResponse>,
                    response: retrofit2.Response<BatchResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val batchResponse = response.body()!!

                        val processedCount = batchResponse.processedCount
                        val duplicateCount = batchResponse.duplicateNumberWorkOrder.size.toLong()
                        val unrelatedCount = batchResponse.unrelatedActivities.size.toLong()
                        val generalErrorCount = batchResponse.errorGeneral.size.toLong()

                        if (duplicateCount == 0L && unrelatedCount == 0L && generalErrorCount == 0L) {
                            // Éxito total
                            _messageResult.postValue(
                                OperationResult.Success(
                                    title = "Registro exitoso",
                                    message = "El archivo se procesó correctamente.\n\nTotal registros: $processedCount"
                                )
                            )
                        } else if (processedCount == 0L) {

                            val message = buildString {
                                append("⚠️ Todos los registros fallaron.\n\n")
                                if (duplicateCount > 0) append("❗ Duplicados: $duplicateCount\n")
                                if (unrelatedCount > 0) append("❗ Actividades sin relación: $unrelatedCount\n")
                                if (generalErrorCount > 0) append("❗ Errores generales: $generalErrorCount")
                            }

                            _messageResult.postValue(
                                OperationResult.Error("Registro Fallido", message))
                        } else {

                            val message = buildString {
                                append("⚠️ El archivo se procesó parcialmente.\n")
                                append("✔️ Correctos: $processedCount\n")
                                if (duplicateCount > 0) append("❗ Duplicados: $duplicateCount\n")
                                if (unrelatedCount > 0) append("❗ Actividades sin relación: $unrelatedCount\n")
                                if (generalErrorCount > 0) append("❗ Errores generales: $generalErrorCount")
                            }

                            _messageResult.postValue(OperationResult.Success("Registro Parcial" , message))
                        }

                    } else {
                        val errorDto = ErrorResponse.extractErrorDto(response)
                        _btnProcessFileEnabled.postValue(true)
                        _messageResult.postValue(OperationResult.Error("Registro Fallido", errorDto.message))
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<BatchResponse>,
                    t: Throwable
                ) {
                    _isValidating.postValue(ValidationState.ERROR)
                    val message = when {
                        t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                            "No se pudo conectar al servidor. Verifica tu conexión a internet."
                        t.message?.contains("timeout", ignoreCase = true) == true ->
                            "La solicitud tardó demasiado. Intenta nuevamente."
                        else ->
                            "Error de red: ${t.localizedMessage ?: "desconocido"}"
                    }

                    _btnProcessFileEnabled.postValue(true)
                    _messageResult.postValue(
                        OperationResult.Error("Error de Red", message))
                }
            })

    }

    fun clearFileUpload(){
        _isValidating.postValue(ValidationState.NONE)
        _btnProcessFileEnabled.postValue(false)
        listWorkOrderRegister = emptyList()
        _fileSize.postValue("")
        _fileName.postValue("")
    }

}
