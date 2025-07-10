package pe.kabj.app_movil_kabj.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.kabj.app_movil_kabj.data.api.AuthPublicApiService
import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient
import pe.kabj.app_movil_kabj.model.dto.ErrorResponse
import pe.kabj.app_movil_kabj.model.dto.OperationResult
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.model.dto.auth.AuthenticationRequest

class LoginViewModel(
    context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)

    // Usar RetrofitClient con context
    private val retrofitClient = RetrofitClient.getInstance(context)

    // Para login usamos PUBLIC service (no necesita token)
    private val authPublicApiService: AuthPublicApiService =
        retrofitClient.createPublicService(AuthPublicApiService::class.java)

    private val _isLoginButtonPressed: MutableLiveData<Boolean> = MutableLiveData()
    val isLoginButtonPressed: LiveData<Boolean> get() = _isLoginButtonPressed

    private val _isAuthenticating: MutableLiveData<Boolean> = MutableLiveData()
    val isAuthenticating: LiveData<Boolean> get() = _isAuthenticating

    private val _loginResult = MutableLiveData<OperationResult>()
    val loginResult: LiveData<OperationResult> get() = _loginResult

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
                val authResponse = authPublicApiService.authenticate(
                    AuthenticationRequest(
                        username,
                        password,
                        "MOBILE"
                    )
                )

                if (!authResponse.isSuccessful) {
                    val errorDto = ErrorResponse.extractErrorDto(authResponse)
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(OperationResult.Error("Autenticación Fallida", errorDto.message))
                    return@launch
                }

                // Extraer token del header Authorization
                val authHeader = authResponse.headers()["Authorization"]
                val token = authHeader?.removePrefix("Bearer ")?.trim()

                if (token.isNullOrEmpty()) {
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(OperationResult.Error("Autenticación Fallida", "No se genero el Token"))
                    return@launch
                }

                sessionManager.saveAuthToken(token)

                val responseBody = authResponse.body()

                val employeeResponse = responseBody?.authEmployeeResponse
                val userResponse = responseBody?.authUserResponse

                if (employeeResponse == null) {
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(OperationResult.Error("Autenticación Fallida", "No se recibió información del empleado"))
                    return@launch
                }

                if (userResponse == null) {
                    _isLoginButtonPressed.postValue(false)
                    _isAuthenticating.postValue(false)
                    _loginResult.postValue(OperationResult.Error("Autenticación Fallida", "No se recibió información del usuario del empleado"))
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
                _loginResult.postValue(OperationResult.Success(null, null))

            } catch (e: Exception) {
                _isLoginButtonPressed.postValue(false)
                _isAuthenticating.postValue(false)
                _loginResult.postValue(OperationResult.Error("Autenticación", "Error de red o inesperado: ${e.message}"))
            }
        }
    }
}