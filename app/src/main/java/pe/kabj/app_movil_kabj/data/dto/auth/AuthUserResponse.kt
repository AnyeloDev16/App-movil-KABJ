package pe.kabj.app_movil_kabj.data.dto.auth

data class AuthUserResponse(
    val permissions: List<String>,
    val active: Boolean
)
