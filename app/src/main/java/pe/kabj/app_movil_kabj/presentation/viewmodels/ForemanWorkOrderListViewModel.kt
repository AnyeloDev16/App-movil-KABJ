package pe.kabj.app_movil_kabj.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.model.WorkOrderListItem
import java.time.LocalDate

class ForemanWorkOrderListViewModel : ViewModel() {

    private val _workOrders = MutableLiveData<List<WorkOrderListItem>>()
    val workOrders: LiveData<List<WorkOrderListItem>> = _workOrders

    private val allData = (1..3).map { WorkOrderListItem(1, 6645743245, "Descripción de la actividad", "ESTADO",
        LocalDate.now()) }

    var currentPage = 0
    private val pageSize = 20

    var isLastPage = false
    var isLoading = false

    fun loadFirstPage() {
        currentPage = 0
        isLastPage = false
        _workOrders.value = emptyList()
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoading || isLastPage) return

        isLoading = true

        // Simula carga asincrónica
        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, allData.size)

        if (startIndex < endIndex) {
            val nextPage = allData.subList(startIndex, endIndex)
            val updatedList = (_workOrders.value ?: emptyList()) + nextPage
            _workOrders.postValue(updatedList)

            currentPage++

            if (endIndex == allData.size) {
                isLastPage = true
            }
        } else {
            isLastPage = true
        }

        isLoading = false
    }
}
