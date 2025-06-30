package pe.kabj.app_movil_kabj.data.dto

data class ErrorResponse(
    val code: String,
    val status: String,
    val message: String,
    val timestamp: String,
    val path: String
)
