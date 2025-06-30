package pe.kabj.app_movil_kabj.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.databinding.ActivityMainBinding
import pe.kabj.app_movil_kabj.presentation.config.MenuConfigurator
import pe.kabj.app_movil_kabj.presentation.fragments.AssignWorkOrderFragment
import pe.kabj.app_movil_kabj.presentation.fragments.ConsultWorkOrderFragment
import pe.kabj.app_movil_kabj.presentation.fragments.ForemanWorkOrderListFragment
import pe.kabj.app_movil_kabj.presentation.fragments.HomeFragment
import pe.kabj.app_movil_kabj.presentation.fragments.RegisterWorkOrderFragment
import pe.kabj.app_movil_kabj.presentation.viewmodels.MainViewModel

/**
 * Actividad principal de la aplicación luego del inicio de sesión.
 * Configuración de la interfaz y observa los cambios del ViewModel.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigation: NavigationView
    private lateinit var header: View
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var progressOverlay: LinearLayout

    // Variables
    private lateinit var sessionManager: SessionManager
    private lateinit var menuConfigurator: MenuConfigurator

    private val mainViewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(applicationContext) as T
            }
        }
    }

    /**
     * Maneja el botón "Atrás"
     */
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else if (supportFragmentManager.backStackEntryCount > 0) {
                val entry = supportFragmentManager.getBackStackEntryAt(
                    supportFragmentManager.backStackEntryCount - 1
                )

                if (mainViewModel.canNavigateBack(entry.name)) {
                    supportFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        menuConfigurator = MenuConfigurator(sessionManager)

        setupComponents()
        configureSystemBars()
        setupToolbar()
        setupNavigationDrawer()
        setupBackPressedHandling()
        setupUserInfo()
        setupObservers()

        menuConfigurator.configureMenu(navigation)
    }


    /**
     * Configura la información del usuario en el header
     */
    private fun setupUserInfo() {
        val fullUsername = sessionManager.fetchNames()!! + " " + sessionManager.fetchSurnames()!!
        header.findViewById<TextView>(R.id.tv_full_username).text = fullUsername
    }

    /**
     * Observa los cambios del ViewModel
     */
    private fun setupObservers() {
        // Observar cambios en el fragmento actual
        mainViewModel.currentFragment.observe(this) { fragmentType ->
            navigateToFragment(fragmentType)
        }

        // Observar cambios en el título del toolbar
        mainViewModel.toolbarTitle.observe(this) { titleResId ->
            supportActionBar?.title = getString(titleResId)
        }

        // Observar cuando se debe cerrar el drawer
        mainViewModel.shouldCloseDrawer.observe(this) { shouldClose ->
            if (shouldClose) {
                drawerLayout.closeDrawers()
                mainViewModel.resetStates()
            }
        }

        // Observar el elemento seleccionado del menú
        mainViewModel.selectedMenuItemId.observe(this) { menuItemId ->
            navigation.setCheckedItem(menuItemId)
        }

        // Observar cuándo mostrar el diálogo de logout
        mainViewModel.showLogoutDialog.observe(this) { shouldShow ->
            if (shouldShow) {
                showLogoutDialog()
            }
        }

        // Observar cuándo navegar al login
        mainViewModel.navigateToLogin.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                navigateToLogin()
                mainViewModel.resetStates()
            }
        }

        mainViewModel.isLoggingOut.observe(this) { isLoggingOut ->
            progressOverlay.visibility = if (isLoggingOut) View.VISIBLE else View.GONE
        }
    }

    // ──────────────────────────────────────────────
    // CONFIGURACIÓN DE COMPONENTES
    // ──────────────────────────────────────────────

    private fun setupComponents() {
        drawerLayout = binding.drawerLayout
        toolbar = binding.toolbar
        navigation = binding.navView
        header = navigation.getHeaderView(0)
        fragmentContainer = binding.fragmentContainer
        progressOverlay = binding.progressOverlay
    }

    private fun configureSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.statusBarColor = getColor(R.color.toolbar_background_color)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupNavigationDrawer() {
        navigation.bringToFront()

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigation.setNavigationItemSelectedListener { menuItem ->
            val isLogout = menuItem.itemId == R.id.nav_logout

            // Delegar la lógica al ViewModel
            mainViewModel.handleNavigationItemSelected(menuItem.itemId)

            // No marcar logout como seleccionado
            !isLogout
        }
    }

    private fun setupBackPressedHandling() {
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        backPressedCallback.remove()
    }

    // ──────────────────────────────────────────────
    // NAVEGACIÓN ENTRE FRAGMENTOS
    // ──────────────────────────────────────────────

    /**
     * Navega al fragmento según el tipo especificado
     */
    private fun navigateToFragment(fragmentType: String) {
        val fragment = when (fragmentType) {
            "HOME" -> HomeFragment()
            "REGISTER_WORK_ORDER" -> RegisterWorkOrderFragment()
            "CONSULT_WORK_ORDER" -> ConsultWorkOrderFragment()
            "ASSIGN_WORK_ORDER" -> AssignWorkOrderFragment()
            "FOREMAN_WORK_ORDER_LIST" -> ForemanWorkOrderListFragment()
            else -> HomeFragment() // Por defecto
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Muestra el diálogo de confirmación de logout
     */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Está seguro que desea cerrar sesión?")
            .setNegativeButton("Cancelar") { _, _ ->
                mainViewModel.cancelLogout()
            }
            .setPositiveButton("Cerrar sesión") { _, _ ->
                mainViewModel.confirmLogout()
            }
            .show()
    }

    /**
     * Navega al login
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}