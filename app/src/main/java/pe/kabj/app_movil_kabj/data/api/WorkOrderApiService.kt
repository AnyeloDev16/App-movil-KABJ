package pe.kabj.app_movil_kabj.data.api

import okhttp3.MultipartBody
import pe.kabj.app_movil_kabj.data.dto.workorder.BatchAssignResponse
import pe.kabj.app_movil_kabj.data.dto.workorder.BatchResponse
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderAssignRequest
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderGeneralResponse
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderRegisterRequest
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderUpdateStateRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface WorkOrderApiService {

    @Multipart
    @POST("work-orders/excel-validate-register")
    fun excelValidateRegister(@Part file: MultipartBody.Part): Call<List<WorkOrderRegisterRequest>>

    @Multipart
    @POST("work-orders/excel-validate-assign")
    fun excelValidateAssign(@Part file: MultipartBody.Part): Call<List<WorkOrderAssignRequest>>

    @POST("work-orders/batch")
    fun saveAll(@Body workOrderRegisterRequest: List<WorkOrderRegisterRequest>): Call<BatchResponse>

    @PATCH("work-orders/batch/foreman/{idForeman}")
    fun assignForeman(@Path("idForeman") idForeman: Long, @Body workOrderAssignRequest: List<WorkOrderAssignRequest>): Call<BatchAssignResponse>

    @GET("work-orders/{numberWorkOrder}/general")
    fun getWorkOrderGeneral(@Path("numberWorkOrder") numberWorkOrder: Long): Call<WorkOrderGeneralResponse>

    @PATCH("work-orders/state")
    fun updateState(@Body workOrderUpdateStateRequest: WorkOrderUpdateStateRequest): Call<Void>

}