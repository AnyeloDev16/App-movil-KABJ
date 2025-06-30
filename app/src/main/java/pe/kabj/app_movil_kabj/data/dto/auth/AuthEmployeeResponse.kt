package pe.kabj.app_movil_kabj.data.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthEmployeeResponse(
    @SerializedName("id_employee")
    val idEmployee: Long,
    val names: String,
    val surnames: String,
    val gender: String,
    val email: String? = null,
    val phone: String? = null,
    val active: Boolean
)
