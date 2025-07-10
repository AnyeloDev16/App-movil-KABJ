package pe.kabj.app_movil_kabj.model.dto.employee

import com.google.gson.annotations.SerializedName

data class EmployeeForemanResponse (

    @SerializedName("id_employee")
    val idEmployee: Long,

    @SerializedName("names")
    val names: String,

    @SerializedName("surnames")
    val surnames: String

)