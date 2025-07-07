package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.data.api.WorkOrderApiService
import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient
import pe.kabj.app_movil_kabj.data.dto.ErrorResponse
import pe.kabj.app_movil_kabj.data.dto.OperationResult
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderGeneralResponse
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.model.enum.Permission

class ConsultWorkOrderViewModel (
    context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)

    // Usar RetrofitClient con context
    private val retrofitClient = RetrofitClient.getInstance(context)

    private val authWorkOrderApiService: WorkOrderApiService = retrofitClient.createAuthService(WorkOrderApiService::class.java)

    private val _isSearching : MutableLiveData<Boolean> = MutableLiveData()
    val isSearching: LiveData<Boolean> get() = _isSearching

    private val _isEnabledBtnSearch: MutableLiveData<Boolean> = MutableLiveData()
    val isEnabledBtnSearch: LiveData<Boolean> get() = _isEnabledBtnSearch

    private val _workOrder : MutableLiveData<WorkOrderGeneralResponse> = MutableLiveData()
    val workOrder: LiveData<WorkOrderGeneralResponse> get() = _workOrder

    private val _messageResult: MutableLiveData<OperationResult> = MutableLiveData()
    val messageResult: LiveData<OperationResult> get() = _messageResult

    fun searchWorkOrderBy(numberWorkOrder: Long) {

        if(!sessionManager.hasPermission(Permission.MOBILE_WORK_ORDER_SEARCH)){
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para Consultar órdenes de trabajo."))
            return
        }

        if (numberWorkOrder <= 0) {
            _messageResult.postValue(OperationResult.Error("Ingreso erróneo", "No puede buscar una Orden de Trabajo con número negativo"))
        }

        _isEnabledBtnSearch.postValue(false)
        _isSearching.postValue(true)

        authWorkOrderApiService.getWorkOrderGeneral(numberWorkOrder)
            .enqueue(object : retrofit2.Callback<WorkOrderGeneralResponse> {

                override fun onResponse(
                    call: retrofit2.Call<WorkOrderGeneralResponse>,
                    response: retrofit2.Response<WorkOrderGeneralResponse>
                ){

                    if (response.isSuccessful && response.body() != null) {
                        _isSearching.postValue(false)
                        _workOrder.postValue(response.body())
                        _isEnabledBtnSearch.postValue(true)
                    } else {
                        _isSearching.postValue(false)
                        val errorDto = ErrorResponse.extractErrorDto(response)
                        _messageResult.postValue(OperationResult.Error("Registro Fallido", errorDto.message))
                        _isEnabledBtnSearch.postValue(true)
                    }

                }

                override fun onFailure(
                    call: retrofit2.Call<WorkOrderGeneralResponse?>,
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

                    _isSearching.postValue(false)
                    _messageResult.postValue(OperationResult.Error("Error de Red", message))
                    _isEnabledBtnSearch.postValue(true)

                }
            })

    }

}