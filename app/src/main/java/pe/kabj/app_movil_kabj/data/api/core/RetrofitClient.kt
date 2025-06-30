package pe.kabj.app_movil_kabj.data.api.core

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import pe.kabj.app_movil_kabj.data.local.SessionManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor(private val context: Context) {

    companion object {
        private const val BASE_URL = "https://8ee4-38-253-150-222.ngrok-free.app/api/v1/"
        internal val gson: Gson = Gson()
        private var INSTANCE: RetrofitClient? = null

        fun getInstance(context: Context): RetrofitClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RetrofitClient(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val sessionManager by lazy { SessionManager(context) }

    // Interceptor para agregar el token Bearer automáticamente
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val startTime = System.currentTimeMillis()

        // Log del REQUEST
        Log.d("API_LOG", "🚀 ${originalRequest.method()} ${originalRequest.url()}")
        Log.d("API_LOG", "Headers: ${originalRequest.headers()}")

        // Log del JSON enviado
        originalRequest.body()?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val requestJson = buffer.readUtf8()
            Log.d("API_LOG", "📤 JSON enviado: $requestJson")
        }

        val token = sessionManager.fetchAuthToken()
        val request = if (!token.isNullOrEmpty()) {
            Log.d("API_LOG", "🔑 Token agregado: Bearer ${token.take(20)}...")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.d("API_LOG", "⚠️ Sin token de autorización")
            originalRequest
        }

        val response = chain.proceed(request)
        val endTime = System.currentTimeMillis()

        // Log de la RESPONSE
        Log.d("API_LOG", "📥 ${response.code()} ${response.message()} (${endTime - startTime}ms)")

        val responseBody = response.peekBody(Long.MAX_VALUE) // permite leer sin consumir
        val responseJson = responseBody.string()

        if (response.code() >= 400) {
            Log.e("API_LOG", "❌ JSON de error recibido: $responseJson")
        } else {
            Log.d("API_LOG", "📦 JSON recibido: $responseJson")
        }

        response
    }

    private val publicLogInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val startTime = System.currentTimeMillis()

        Log.d("API_LOG", "🚀 ${originalRequest.method()} ${originalRequest.url()}")
        Log.d("API_LOG", "🗣️ Headers: ${originalRequest.headers()}")

        originalRequest.body()?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val requestJson = buffer.readUtf8()
            Log.d("API_LOG", "📤 JSON enviado: $requestJson")
        }

        val response = chain.proceed(originalRequest)
        val endTime = System.currentTimeMillis()

        Log.d("API_LOG", "📥 ${response.code()} ${response.message()} (${endTime - startTime}ms)")

        val responseBody = response.peekBody(Long.MAX_VALUE)
        val responseJson = responseBody.string()

        if (response.code() >= 400) {
            Log.e("API_LOG", "❌ JSON de error recibido: $responseJson")
        } else {
            Log.d("API_LOG", "📦 JSON recibido: $responseJson")
        }

        response
    }


    // Cliente CON token automático
    private val okHttpClientAuth  = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    // Cliente SIN token (para métodos públicos)
    private val publicClient = OkHttpClient.Builder()
        .addInterceptor(publicLogInterceptor)
        .build()

    // Retrofit CON autenticación
    private val authenticatedRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClientAuth)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit SIN autenticación
    private val publicRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(publicClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Crear servicio CON token automático
    fun <T> createAuthService(serviceClass: Class<T>): T {
        return authenticatedRetrofit.create(serviceClass)
    }

    // Crear servicio SIN token
    fun <T> createPublicService(serviceClass: Class<T>): T {
        return publicRetrofit.create(serviceClass)
    }

}
