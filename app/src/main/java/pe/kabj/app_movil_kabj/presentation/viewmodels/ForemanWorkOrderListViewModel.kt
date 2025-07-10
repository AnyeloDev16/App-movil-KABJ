package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.data.api.WorkOrderApiService
import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient
import pe.kabj.app_movil_kabj.model.dto.ErrorResponse
import pe.kabj.app_movil_kabj.model.dto.OperationResult
import pe.kabj.app_movil_kabj.model.dto.PageResponse
import pe.kabj.app_movil_kabj.model.dto.workorder.WorkOrderForemanResponse
import pe.kabj.app_movil_kabj.model.dto.workorder.WorkOrderGeneralResponse
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.model.enum.Permission
import retrofit2.Call
import retrofit2.Response

class ForemanWorkOrderListViewModel(
    context: Context
) : ViewModel() {

    companion object{
        private const val NUMBER_ITEMS_PER_PAGE : Long = 5
    }

    private val sessionManager = SessionManager(context)

    // Usar RetrofitClient con context
    private val retrofitClient = RetrofitClient.getInstance(context)

    private val authWorkOrderApiService: WorkOrderApiService = retrofitClient.createAuthService(WorkOrderApiService::class.java)

    private val _workOrder : MutableLiveData<WorkOrderGeneralResponse> = MutableLiveData()
    val workOrder: LiveData<WorkOrderGeneralResponse> get() = _workOrder

    private val _items = MutableLiveData<List<WorkOrderForemanResponse>>()
    val items: LiveData<List<WorkOrderForemanResponse>> get() = _items

    private val _actualPage = MutableLiveData<Long>(0)
    val actualPage: LiveData<Long> = _actualPage

    private val _totalPage = MutableLiveData<Long>(0)
    val totalPage: LiveData<Long> = _totalPage

    private val _btnEnableBtnPreviousPage = MutableLiveData<Boolean>()
    val btnEnableBtnPreviousPage: LiveData<Boolean> = _btnEnableBtnPreviousPage

    private val _btnEnableBtnNextPage = MutableLiveData<Boolean>()
    val btnEnableBtnNextPage: LiveData<Boolean> = _btnEnableBtnNextPage

    private val _messageResult = MutableLiveData<OperationResult>()
    val messageResult: LiveData<OperationResult> get() = _messageResult

    fun gotToActualPage(){
        userHasPermissionWorkOrdersAssigned()
        disableBtns()
        loadPage(actualPage.value!!)
    }

    fun goToPreviousPage(){
        userHasPermissionWorkOrdersAssigned()
        disableBtns()
        val newPage = actualPage.value!! - 1
        if(newPage < 0){
            _messageResult.postValue(OperationResult.Error("Error al cambiar de pagina", "No se puede ir a la pagina 0"))
            return
        }
        loadPage(newPage)
    }

    fun goToNextPage(){
        userHasPermissionWorkOrdersAssigned()
        disableBtns()
        val newPage = actualPage.value!! + 1
        val totalPage = totalPage.value!!
        if(newPage > totalPage){
            _messageResult.postValue(OperationResult.Error("Error al cambiar de pagina", "No se puede ir a la pagina " + (totalPage + 1) ))
            return
        }
        loadPage(newPage)
    }

    fun searchWorkOrderBy(numberWorkOrder: Long) {

        userHasPermissionWorkOrdersAssignedDetails()

        if (numberWorkOrder <= 0) {
            _messageResult.postValue(OperationResult.Error("Ingreso erróneo", "No puede buscar una Orden de Trabajo con número negativo"))
        }

        authWorkOrderApiService.getWorkOrderGeneral(numberWorkOrder)
            .enqueue(object : retrofit2.Callback<WorkOrderGeneralResponse> {

                override fun onResponse(
                    call: Call<WorkOrderGeneralResponse>,
                    response: Response<WorkOrderGeneralResponse>
                ){

                    if (response.isSuccessful && response.body() != null) {
                        _workOrder.postValue(response.body())
                    } else {
                        val errorDto = ErrorResponse.extractErrorDto(response)
                        _messageResult.postValue(OperationResult.Error("Registro Fallido", errorDto.message))
                    }

                }

                override fun onFailure(
                    call: Call<WorkOrderGeneralResponse?>,
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

                }
            })

    }

    private fun disableBtns(){
        _btnEnableBtnPreviousPage.postValue(false)
        _btnEnableBtnNextPage.postValue(false)
    }

    private fun loadPage(numberPage: Long){
        authWorkOrderApiService.getAllWorkOrderAssignedByForeman(numberPage, NUMBER_ITEMS_PER_PAGE)
            .enqueue(object : retrofit2.Callback<PageResponse<WorkOrderForemanResponse>> {

                override fun onResponse(
                    call: Call<PageResponse<WorkOrderForemanResponse>?>,
                    response: Response<PageResponse<WorkOrderForemanResponse>?>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val pageData = response.body()!!

                        val content = pageData.content
                        val page = pageData.page
                        val totalPages = page.totalPages
                        val currentPage = page.number

                        // Actualizar LiveData del listado
                        _items.postValue(content)
                        updateInfoPagesAndBtns(currentPage, totalPages)

                        // También puedes emitir un mensaje de éxito o actualizar el estado de la UI
                        _messageResult.postValue(OperationResult.Success("Datos cargados", "Página ${currentPage + 1} de $totalPages"))
                    } else {
                        val errorDto = ErrorResponse.extractErrorDto(response)
                        _messageResult.postValue(OperationResult.Error("Registro Fallido", errorDto.message))
                        updateStateBtns()
                    }
                }

                override fun onFailure(
                    call: Call<PageResponse<WorkOrderForemanResponse>?>,
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

                    _messageResult.postValue(
                        OperationResult.Error("Error de Red", message))
                    updateStateBtns()
                }

            })
    }

    private fun updateInfoPagesAndBtns(actualPage: Long, totalPage: Long) {
        _actualPage.postValue(actualPage)
        _totalPage.postValue(totalPage)
        _btnEnableBtnPreviousPage.postValue(actualPage > 0)
        _btnEnableBtnNextPage.postValue(actualPage < totalPage - 1)
    }

    private fun updateStateBtns(){

        val actualPage = actualPage.value!!
        val totalPage = totalPage.value!!

        _btnEnableBtnPreviousPage.postValue(actualPage > 0)
        _btnEnableBtnNextPage.postValue(actualPage < totalPage - 1)

    }

    private fun userHasPermissionWorkOrdersAssigned() {
        if(!sessionManager.hasPermission(Permission.MOBILE_WORK_ORDER_ASSIGNED)){
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos Consultar sus Ordenes de Trabajo asignado"))
            return
        }
    }

    private fun userHasPermissionWorkOrdersAssignedDetails() {
        if(!sessionManager.hasPermission(Permission.MOBILE_WORK_ORDER_ASSIGNED_DETAIL)){
            _messageResult.postValue(OperationResult.Error("Acceso Denegado", "No tienes permisos para Ver los detalles de las órdenes de trabajo."))
            return
        }
    }

}
