package pe.kabj.app_movil_kabj.data.api

import pe.kabj.app_movil_kabj.data.dto.auth.AuthenticationRequest
import pe.kabj.app_movil_kabj.data.dto.auth.AuthenticationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/log-in")
    suspend fun authenticate(@Body authenticationRequest: AuthenticationRequest): Response<AuthenticationResponse>

}