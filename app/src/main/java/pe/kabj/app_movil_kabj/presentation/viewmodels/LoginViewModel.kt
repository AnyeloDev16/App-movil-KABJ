package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.kabj.app_movil_kabj.data.api.AuthApiService
import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient
import pe.kabj.app_movil_kabj.data.dto.ErrorResponse
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.data.dto.auth.AuthenticationRequest

class LoginViewModel(
    context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)

    // Usar RetrofitClient con context
    private val retrofitClient = RetrofitClient.getInstance(context)

    // Para login usamos PUBLIC service (no necesita token)
    private var authApiService: AuthApiService = retrofitClient.createPublicService(AuthApiService::class.java)

    private val _isLoginButtonPressed: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginButtonPressed: LiveData<Boolean> get() = _isLoginButtonPressed

    private val _isAuthenticating: MutableLiveData<Boolean> = MutableLiveData()
    val isAuthenticating: LiveData<Boolean> get() = _isAuthenticating

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    fun isLoginFormValid(username: String, password: String): Boolean {
        _isLoginButtonPressed.value = true

        return if (username.isNotBlank() && password.isNotBlank()) {
            true
        } else {
            _isLoginButtonPressed.value = false
            false
        }
    }

    fun authenticateUser(username: String, password: String) {
        viewModelScope.launch {
            _isAuthenticating.postValue(true)

            try {
                val authResponse = authApiService.authenticate(AuthenticationRequest(username, password))

                if (!authResponse.isSuccessful) {
                    val errorDto = extractErrorDto(authResponse)
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(LoginResult.Error(errorDto))
                    return@launch
                }

                // Extraer token del header Authorization
                val authHeader = authResponse.headers()["Authorization"]
                val token = authHeader?.removePrefix("Bearer ")?.trim()

                if (token.isNullOrEmpty()) {
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(
                        LoginResult.Error(
                            ErrorResponse(
                                code = "NO_TOKEN",
                                status = "BAD_RESPONSE",
                                message = "No se recibió token de autenticación.",
                                timestamp = System.currentTimeMillis().toString(),
                                path = "/auth/log-in"
                            )
                        )
                    )
                    return@launch
                }

                sessionManager.saveAuthToken(token)

                val responseBody = authResponse.body()
                Log.d("API_LOG", responseBody.toString())

                val employeeResponse = responseBody?.authEmployeeResponse
                val userResponse = responseBody?.authUserResponse

                if (employeeResponse == null) {
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(
                        LoginResult.Error(
                            ErrorResponse(
                                code = "NO_EMPLOYEE_DATA",
                                status = "BAD_RESPONSE",
                                message = "No se recibió información del empleado.",
                                timestamp = System.currentTimeMillis().toString(),
                                path = "/auth/log-in"
                            )
                        )
                    )
                    return@launch
                }

                if (userResponse == null) {
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(
                        LoginResult.Error(
                            ErrorResponse(
                                code = "NO_USER_DATA",
                                status = "BAD_RESPONSE",
                                message = "No se recibió información del usuario del empleado.",
                                timestamp = System.currentTimeMillis().toString(),
                                path = "/auth/log-in"
                            )
                        )
                    )
                    return@launch
                }

                sessionManager.saveUserInfo(
                    employeeResponse.idEmployee,
                    employeeResponse.names,
                    employeeResponse.surnames,
                    employeeResponse.names,
                    employeeResponse.email,
                    employeeResponse.phone,
                    userResponse.active,
                    userResponse.permissions
                )

                _isLoginButtonPressed.postValue(false)
                _isAuthenticating.postValue(false)
                _loginResult.postValue(LoginResult.Success())

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error durante autenticación", e)
                _isLoginButtonPressed.postValue(false)
                _isAuthenticating.postValue(false)
                _loginResult.postValue(
                    LoginResult.Error(
                        ErrorResponse(
                            code = "EXCEPTION",
                            status = "ERROR",
                            message = "Error de red o inesperado: ${e.message}",
                            timestamp = System.currentTimeMillis().toString(),
                            path = "/auth/log-in"
                        )
                    )
                )
            }
        }
    }


    private fun extractErrorDto(response: retrofit2.Response<*>): ErrorResponse {
        return try {
            val errorBodyStr = response.errorBody()?.string()
            if (!errorBodyStr.isNullOrEmpty()) {
                RetrofitClient.gson.fromJson(errorBodyStr, ErrorResponse::class.java)
            } else {
                ErrorResponse(
                    code = "HTTP_${response.code()}",
                    status = response.message(),
                    message = "Error sin body",
                    timestamp = "",
                    path = ""
                )
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Fallo al parsear errorBody", e)
            ErrorResponse(
                code = "HTTP_${response.code()}",
                status = response.message(),
                message = "Error inesperado al procesar el error",
                timestamp = "",
                path = ""
            )
        }
    }

}

sealed class LoginResult {
    class Success : LoginResult()
    class Error(val errorResponse: ErrorResponse) : LoginResult()
}