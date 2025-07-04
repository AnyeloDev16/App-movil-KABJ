package pe.kabj.app_movil_kabj.data.api

import okhttp3.MultipartBody
import pe.kabj.app_movil_kabj.data.dto.workorder.BatchResponse
import pe.kabj.app_movil_kabj.data.dto.workorder.WorkOrderRegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WorkOrderApiService {

    @Multipart
    @POST("work-orders/excel-validate")
    fun excelValidate(@Part file: MultipartBody.Part): Call<List<WorkOrderRegisterRequest>>

    @POST("work-orders/batch")
    fun saveAll(@Body workOrderRegisterRequest: List<WorkOrderRegisterRequest>): Call<BatchResponse>

}