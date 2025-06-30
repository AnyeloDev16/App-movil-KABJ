package pe.kabj.app_movil_kabj.data.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthenticationResponse(
    @SerializedName("auth_employee_response")
    val authEmployeeResponse: AuthEmployeeResponse,
    @SerializedName("auth_user_response")
    val authUserResponse: AuthUserResponse
)



