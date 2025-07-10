package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.data.api.WorkOrderApiService
import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient
import pe.kabj.app_movil_kabj.model.dto.OperationResult
import pe.kabj.app_movil_kabj.model.dto.workorder.WorkOrderGeneralResponse
import pe.kabj.app_movil_kabj.model.dto.workorder.WorkOrderUpdateStateRequest
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.model.enum.Permission
import pe.kabj.app_movil_kabj.presentation.enums.WorkOrderState
import retrofit2.Call
import retrofit2.Response

class WorkOrderDetailForemanViewModel(
    context: Context,
    private val workOrderGeneralResponse: WorkOrderGeneralResponse
) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val retrofitClient = RetrofitClient.getInstance(context)
    private val workOrderApiService: WorkOrderApiService = retrofitClient.createAuthService(WorkOrderApiService::class.java)

    private var actualState = workOrderGeneralResponse.state
    private var selectedState: WorkOrderState = actualState

    private val _hasChange = MutableLiveData<Boolean>()
    val hasChange: LiveData<Boolean> get() = _hasChange

    private val _messageResult = MutableLiveData<OperationResult>()
    val messageResult: LiveData<OperationResult> get() = _messageResult

    fun selectNewState(newState: WorkOrderState){

        selectedState = newState
        _hasChange.postValue(actualState != newState)

    }

    fun userHasPermissionChangeState(): Boolean {
        return sessionManager.hasPermission(Permission.MOBILE_WORK_ORDER_CHANGE_STATE)
    }

    fun saveChanges(){

        if(!userHasPermissionChangeState()){
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para Cambiar el estado de una órden de trabajo."))
            return
        }

        var workOrderUpdateStateRequest = WorkOrderUpdateStateRequest(
            workOrderGeneralResponse.numberWorkOrder,
            selectedState
        )

        workOrderApiService.updateState(workOrderUpdateStateRequest)
            .enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(
                    call: Call<Void?>,
                    response: Response<Void?>
                ) {
                    if (response.isSuccessful) {
                        actualState = selectedState
                        _hasChange.postValue(false)
                        _messageResult.postValue(OperationResult.Success(
                            "Actualización Exitosa",
                            "Se cambio el estado de la Orden a : " + selectedState.toString())
                        )
                    } else {
                        _messageResult.postValue(
                            OperationResult.Error("Error", "Ocurrio un error inesperado."))
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
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

}