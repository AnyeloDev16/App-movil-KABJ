package pe.kabj.app_movil_kabj.model.dto

sealed class OperationResult {
    class Success(val title: String?, val message: String?) : OperationResult()
    class Error(val title: String, val message: String) : OperationResult()
}