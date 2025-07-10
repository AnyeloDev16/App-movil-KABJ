package pe.kabj.app_movil_kabj.model.dto

import pe.kabj.app_movil_kabj.data.api.core.RetrofitClient

data class ErrorResponse(
    val code: String,
    val status: String,
    val message: String,
    val timestamp: String,
    val path: String
) {
    companion object {
        fun extractErrorDto(response: retrofit2.Response<*>): ErrorResponse {
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
}


