package pe.kabj.app_movil_kabj.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pe.kabj.app_movil_kabj.R

/**
 * ViewModel para la actividad principal.
 * Maneja la lógica de negocio relacionada con:
 * - Navegación entre fragmentos
 * - Estados de la interfaz
 * - Control del menú lateral
 */
class MainViewModel : ViewModel() {

    // Estados de navegación
    private val _currentFragment = MutableLiveData<String>()
    val currentFragment: LiveData<String> = _currentFragment

    private val _toolbarTitle = MutableLiveData<Int>()
    val toolbarTitle: LiveData<Int> = _toolbarTitle

    private val _shouldCloseDrawer = MutableLiveData<Boolean>()
    val shouldCloseDrawer: LiveData<Boolean> = _shouldCloseDrawer

    private val _showLogoutDialog = MutableLiveData<Boolean>()
    val showLogoutDialog: LiveData<Boolean> = _showLogoutDialog

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    // Estados del menú
    private val _selectedMenuItemId = MutableLiveData<Int>()
    val selectedMenuItemId: LiveData<Int> = _selectedMenuItemId

    // Control de back stack
    private val backNavigableFragments = setOf(
        "WorkOrderDetail",
        "WorkOrderDetailForeman"
    )

    init {
        navigateToHome()
    }

    /**
     * Maneja la selección de elementos del menú lateral
     */
    fun handleNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> {
                navigateToHome()
            }
            R.id.nav_register_work_orders -> {
                navigateToRegisterWorkOrders()
            }
            R.id.nav_consult_work_orders -> {
                navigateToConsultWorkOrders()
            }
            R.id.nav_assign_work_orders -> {
                navigateToAssignWorkOrders()
            }
            R.id.nav_list_work_orders -> {
                navigateToForemanWorkOrderList()
            }
            R.id.nav_logout -> {
                showLogoutDialog()
            }
        }
    }

    /**
     * Navega a la pantalla principal
     */
    fun navigateToHome() {
        _currentFragment.value = "HOME"
        _toolbarTitle.value = R.string.title_home
        _selectedMenuItemId.value = R.id.nav_home
        _shouldCloseDrawer.value = true
    }

    /**
     * Navega al fragmento de registro de órdenes de trabajo
     */
    private fun navigateToRegisterWorkOrders() {
        _currentFragment.value = "REGISTER_WORK_ORDER"
        _toolbarTitle.value = R.string.title_register_work_order
        _selectedMenuItemId.value = R.id.nav_register_work_orders
        _shouldCloseDrawer.value = true
    }

    /**
     * Navega al fragmento de consulta de órdenes de trabajo
     */
    private fun navigateToConsultWorkOrders() {
        _currentFragment.value = "CONSULT_WORK_ORDER"
        _toolbarTitle.value = R.string.title_consult_work_order
        _selectedMenuItemId.value = R.id.nav_consult_work_orders
        _shouldCloseDrawer.value = true
    }

    /**
     * Navega al fragmento de asignación de órdenes de trabajo
     */
    private fun navigateToAssignWorkOrders() {
        _currentFragment.value = "ASSIGN_WORK_ORDER"
        _toolbarTitle.value = R.string.title_assign_work_order
        _selectedMenuItemId.value = R.id.nav_assign_work_orders
        _shouldCloseDrawer.value = true
    }

    /**
     * Navega al fragmento de lista de órdenes de trabajo del capataz
     */
    private fun navigateToForemanWorkOrderList() {
        _currentFragment.value = "FOREMAN_WORK_ORDER_LIST"
        _toolbarTitle.value = R.string.title_foreman_work_order_list
        _selectedMenuItemId.value = R.id.nav_list_work_orders
        _shouldCloseDrawer.value = true
    }

    /**
     * Muestra el diálogo de confirmación de cierre de sesión
     */
    private fun showLogoutDialog() {
        _showLogoutDialog.value = true
    }

    /**
     * Maneja la confirmación de cierre de sesión
     */
    fun confirmLogout() {
        _navigateToLogin.value = true
        _showLogoutDialog.value = false
    }

    /**
     * Maneja la cancelación del cierre de sesión
     */
    fun cancelLogout() {
        _showLogoutDialog.value = false
    }

    /**
     * Determina si se puede navegar hacia atrás en el fragmento actual
     */
    fun canNavigateBack(currentFragmentTag: String?): Boolean {
        return currentFragmentTag in backNavigableFragments
    }

    /**
     * Resetea los estados después de procesar las acciones
     */
    fun resetStates() {
        _shouldCloseDrawer.value = false
        _navigateToLogin.value = false
    }
}