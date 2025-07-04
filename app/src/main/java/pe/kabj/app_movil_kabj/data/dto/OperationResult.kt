package pe.kabj.app_movil_kabj.data.dto

sealed class OperationResult {
    class Success(val title: String?, val message: String?) : OperationResult()
    class Error(val title: String, val message: String) : OperationResult()
}