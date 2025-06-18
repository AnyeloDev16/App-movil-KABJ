package pe.kabj.app_movil_kabj.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConsultWorkOrderViewModel : ViewModel() {

    val isSearching : MutableLiveData<Boolean> = MutableLiveData()
    val workOrder : MutableLiveData<Any?> = MutableLiveData()

    fun searchWorkOrderBy(numberWorkOrder: Long) {
        isSearching.value = true
        // Buscar workorder
        viewModelScope.launch {
            // Simula tiempo de espera (por ejemplo 2 segundos)
            delay(2000)
            isSearching.value = false
            // Aquí simularías el resultado de la búsqueda
            workOrder.postValue(Any())
        }
    }

}