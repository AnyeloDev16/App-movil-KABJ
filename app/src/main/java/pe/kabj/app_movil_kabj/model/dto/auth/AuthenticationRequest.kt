package pe.kabj.app_movil_kabj.model.dto.auth

data class AuthenticationRequest (
    private val username: String,
    private val password: String,
    private val platform: String
)