package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.data.api.EmployeeApiService
import pe.kabj.app_movil_kabj.data.api.WorkOrderApiService
import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient
import pe.kabj.app_movil_kabj.data.dto.ErrorResponse
import pe.kabj.app_movil_kabj.data.dto.OperationResult
import pe.kabj.app_movil_kabj.data.dto.employee.EmployeeForemanResponse
import pe.kabj.app_movil_kabj.data.dto.workorder.BatchAssignResponse
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderAssignRequest
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.model.enum.Permission
import pe.kabj.app_movil_kabj.presentation.enums.ValidationState
import pe.kabj.app_movil_kabj.presentation.utils.ModalDialogUtils
import pe.kabj.app_movil_kabj.util.FileUtils
import pe.kabj.app_movil_kabj.util.UploadFileData
import retrofit2.Response

class AssignWorkOrderViewModel(
    private val context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val retrofitClient = RetrofitClient.getInstance(context)
    private val workOrderApiService: WorkOrderApiService = retrofitClient.createAuthService(WorkOrderApiService::class.java)
    private val employeeApiService: EmployeeApiService = retrofitClient.createAuthService(EmployeeApiService::class.java)

    private lateinit var listWorkOrderAssign: List<WorkOrderAssignRequest>

    private val _foremanList = MutableLiveData<List<EmployeeForemanResponse>>()
    val foremanList: LiveData<List<EmployeeForemanResponse>> get() = _foremanList

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
        listWorkOrderAssign = emptyList()

        if (!userHasPermissionAssignForeman()) {
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para Asignar órdenes de trabajo."))
            return
        }

        val file: UploadFileData = FileUtils.createUploadFileData(context, uri)

        _fileName.postValue(file.fileName)
        _fileSize.postValue(file.fileSizeFormatted)
        _isValidating.postValue(ValidationState.VALIDATING)

        workOrderApiService.excelValidateAssign(file.multipartBody)
            .enqueue(object : retrofit2.Callback<List<WorkOrderAssignRequest>> {

                override fun onResponse(
                    call: retrofit2.Call<List<WorkOrderAssignRequest>>,
                    response: Response<List<WorkOrderAssignRequest>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        _isValidating.postValue(ValidationState.SUCCESS)
                        listWorkOrderAssign = response.body()!!
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
                    call: retrofit2.Call<List<WorkOrderAssignRequest>>,
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

    fun processSelectedFile(foremanSelected: EmployeeForemanResponse){

        if (!userHasPermissionAssignForeman()) {
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para Asignar órdenes de trabajo."))
            return
        }

        if(listWorkOrderAssign.isEmpty()){
            _messageResult.postValue(OperationResult.Error("Error Inesperado", "No se cuentan con registros de Ordenes de Trabajo a Asignar."))
            return
        }

        ModalDialogUtils.showConfirmDialog(context, "Las ordenes subidas se le asignaran al Capataz:\n\n- " + foremanSelected.names + " " + foremanSelected.surnames){

            _btnProcessFileEnabled.postValue(false)

            workOrderApiService.assignForeman(foremanSelected.idEmployee, listWorkOrderAssign)
                .enqueue(object : retrofit2.Callback<BatchAssignResponse> {

                    override fun onResponse(
                        call: retrofit2.Call<BatchAssignResponse>,
                        response: Response<BatchAssignResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val batchResponse = response.body()!!

                            val processedCount = batchResponse.processedCount
                            val notFoundIdsCount = batchResponse.notFoundIds.size.toLong()

                            if (notFoundIdsCount == 0L) {
                                // Éxito total
                                _messageResult.postValue(
                                    OperationResult.Success(
                                        title = "Asignación exitosa",
                                        message = "El archivo se procesó correctamente.\n\nTotal asigandos: $processedCount"
                                    )
                                )
                            } else if (processedCount == 0L) {

                                val message = buildString {
                                    append("⚠️ Todos las asignaciones fallaron.\n\n")
                                    if (notFoundIdsCount > 0) append("❗ No encontrados: $notFoundIdsCount\n")
                                }

                                _messageResult.postValue(
                                    OperationResult.Error("Registro Fallido", message))
                            } else {

                                val message = buildString {
                                    append("⚠️ El archivo se procesó parcialmente.\n")
                                    append("✔️ Correctos: $processedCount\n")
                                    if (notFoundIdsCount > 0) append("❗ No encontrados: $notFoundIdsCount\n")
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
                        call: retrofit2.Call<BatchAssignResponse>,
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

    }

    fun getAllForemanActive() {

        if (!userHasPermissionConsultForeman()) {
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para obtener la lista de Capataces"))
            _foremanList.postValue(emptyList())
            return
        }

        employeeApiService.getAllForemanActive()
            .enqueue(object : retrofit2.Callback<List<EmployeeForemanResponse>> {
                override fun onResponse(
                    call: retrofit2.Call<List<EmployeeForemanResponse>>,
                    response: Response<List<EmployeeForemanResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        _foremanList.postValue(response.body())
                    } else {
                        val errorDto = ErrorResponse.extractErrorDto(response)
                        _messageResult.postValue(OperationResult.Error("Consulta Fallida", errorDto.message))
                        _foremanList.postValue(emptyList())
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<List<EmployeeForemanResponse>>,
                    t: Throwable
                ) {
                    val message = when {
                        t.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                            "No se pudo conectar al servidor. Verifica tu conexión a internet."
                        t.message?.contains("timeout", ignoreCase = true) == true ->
                            "La solicitud tardó demasiado. Intenta nuevamente."
                        else ->
                            "Error de red: ${t.localizedMessage ?: "desconocido"}"
                    }

                    _messageResult.postValue(OperationResult.Error("Error de Red", message))
                    _foremanList.postValue(emptyList())
                }
            })
    }

    fun clearFileUpload(){
        _isValidating.postValue(ValidationState.NONE)
        _btnProcessFileEnabled.postValue(false)
        listWorkOrderAssign = emptyList()
        _fileSize.postValue("")
        _fileName.postValue("")
    }

    fun userHasPermissionAssignForeman(): Boolean{
        return sessionManager.hasPermission(Permission.MOBILE_WORK_ORDER_ASSIGN)
    }

    fun userHasPermissionConsultForeman(): Boolean{
        return sessionManager.hasPermission(Permission.MOBILE_FOREMAN_CONSULT)
    }

}