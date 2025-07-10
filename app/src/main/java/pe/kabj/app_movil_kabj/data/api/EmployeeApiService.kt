package pe.kabj.app_movil_kabj.data.api

import pe.kabj.app_movil_kabj.model.dto.employee.EmployeeForemanResponse
import retrofit2.Call
import retrofit2.http.GET

interface EmployeeApiService {

    @GET("employees/active-foremen")
    fun getAllForemanActive(): Call<List<EmployeeForemanResponse>>

}