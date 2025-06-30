package pe.kabj.app_movil_kabj.data.api

import retrofit2.Response
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/log-out/own-session")
    suspend fun logOutOwnSession() : Response<Void>

}