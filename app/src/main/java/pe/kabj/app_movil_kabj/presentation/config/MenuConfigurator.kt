package pe.kabj.app_movil_kabj.presentation.config

import com.google.android.material.navigation.NavigationView
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.data.local.SessionManager

class MenuConfigurator(
    private val sessionManager: SessionManager
) {

    fun configureMenu(navigationView: NavigationView) {
        val menu = navigationView.menu

        menu.findItem(R.id.nav_home)?.isVisible = sessionManager.hasPermission("MOBILE_HOME_VIEW")
        menu.findItem(R.id.nav_register_work_orders)?.isVisible = sessionManager.hasPermission("MOBILE_WORK_ORDER_REGISTER_VIEW")
        menu.findItem(R.id.nav_consult_work_orders)?.isVisible = sessionManager.hasPermission("MOBILE_WORK_ORDER_CONSULT_VIEW")
        menu.findItem(R.id.nav_assign_work_orders)?.isVisible = sessionManager.hasPermission("MOBILE_WORK_ORDER_ASSIGNED_LIST_VIEW")
        menu.findItem(R.id.nav_list_work_orders)?.isVisible = sessionManager.hasPermission("MOBILE_WORK_ORDER_LIST_VIEW")

    }
}
