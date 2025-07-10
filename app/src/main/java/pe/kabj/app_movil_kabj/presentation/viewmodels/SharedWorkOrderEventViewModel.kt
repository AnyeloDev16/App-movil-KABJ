package pe.kabj.app_movil_kabj.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedWorkOrderEventViewModel : ViewModel() {

    private val _shouldRefreshList: MutableLiveData<Boolean> = MutableLiveData()
    val shouldRefreshList: LiveData<Boolean> get() = _shouldRefreshList

    fun triggerRefresh() {
        _shouldRefreshList.postValue(true)
    }

    fun resetRefreshFlag() {
        _shouldRefreshList.postValue(false)
    }

}
