package pe.kabj.app_movil_kabj.data.api

import pe.kabj.app_movil_kabj.model.dto.auth.AuthenticationRequest
import pe.kabj.app_movil_kabj.model.dto.auth.AuthenticationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthPublicApiService {

    @POST("auth/log-in")
    suspend fun authenticate(@Body authenticationRequest: AuthenticationRequest): Response<AuthenticationResponse>

}