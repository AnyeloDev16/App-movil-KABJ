package pe.kabj.app_movil_kabj.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _visibleCards: MutableLiveData<Set<String>> = MutableLiveData()
    val visibleCards: LiveData<Set<String>> get() = _visibleCards

    fun loadVisibleCards(userPermissions: Set<String>) {
        val visible = mutableSetOf<String>()

        if (userPermissions.any { it == "MOBILE_WORK_ORDER_REGISTER_VIEW" }) visible.add("REGISTER")
        if (userPermissions.any { it == "MOBILE_WORK_ORDER_CONSULT_VIEW" }) visible.add("CONSULT")
        if (userPermissions.any { it == "MOBILE_WORK_ORDER_ASSIGNED_LIST_VIEW" }) visible.add("ASSIGN")
        if (userPermissions.any { it == "MOBILE_WORK_ORDER_LIST_VIEW" }) visible.add("FOREMAN")

        _visibleCards.postValue(visible)
    }

}