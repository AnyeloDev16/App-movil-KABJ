package pe.kabj.app_movil_kabj.data.dto.auth

data class AuthUserResponse(
    val permissions: Set<String>,
    val active: Boolean
)
