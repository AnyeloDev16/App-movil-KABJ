package pe.kabj.app_movil_kabj.presentation.activities

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.databinding.ActivityMainBinding
import pe.kabj.app_movil_kabj.presentation.fragments.RegisterWorkOrderFragment

/**
 * Actividad principal de la aplicación luego del inicio de sesión.
 * Se encarga de configurar la interfaz principal, incluyendo:
 * - La barra de herramientas (Toolbar)
 * - El menú lateral (Navigation Drawer)
 * - El contenedor de fragments
 * - La configuración del sistema inmersivo (ocultando barra de navegación)
 * - La gestión personalizada del botón "Atrás"
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigation: NavigationView
    private lateinit var header: View
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var username: String

    /**
     * Callback personalizado para manejar el comportamiento del botón "Atrás".
     * Si el menú lateral está abierto, lo cierra. Si no, delega el comportamiento por defecto.
     */
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    /**
     * Método principal de inicialización. Se ejecuta al crear la actividad.
     * Configura la interfaz, los componentes de navegación y carga la vista inicial.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupComponents()
        configureSystemBars()
        setupToolbar()
        setupNavigationDrawer()
        setupBackPressedHandling()

        username = intent.getStringExtra("username") ?: "Usuario"
        header.findViewById<TextView>(R.id.tv_username).text = username

        navigation.setCheckedItem(R.id.nav_home)
        navigateToHome()

    }

    // ──────────────────────────────────────────────
    // CONFIGURACIÓN DE COMPONENTES
    // ──────────────────────────────────────────────

    /**
     * Inicializa los componentes visuales mediante ViewBinding.
     */
    private fun setupComponents() {
        drawerLayout = binding.drawerLayout
        toolbar = binding.toolbar
        navigation = binding.navView
        header = navigation.getHeaderView(0)
        fragmentContainer = binding.fragmentContainer
    }

    /**
     * Configura el comportamiento de la barra de estado y la barra de navegación
     * para ocultar los botones del sistema y permitir deslizamiento para mostrarlos.
     */
    private fun configureSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.statusBarColor = getColor(R.color.toolbar_background_color)
    }

    /**
     * Configura la barra de herramientas (Toolbar) como ActionBar
     * y activa los botones de navegación.
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    /**
     * Configura el Navigation Drawer, incluyendo el toggle (ícono hamburguesa)
     * y el manejo de selección de elementos del menú lateral.
     */
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
            handleNavigationItemSelected(menuItem.itemId)
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    /**
     * Registra el comportamiento personalizado del botón físico/gestual "Atrás".
     */
    private fun setupBackPressedHandling() {
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun setTitleToolbar(titleId: Int) {
        toolbar.title = getString(titleId)
    }

    // ──────────────────────────────────────────────
    // NAVEGACIÓN ENTRE VISTAS
    // ──────────────────────────────────────────────

    /**
     * Maneja la navegación según el ítem seleccionado del menú lateral.
     */
    private fun handleNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> navigateToHome()
            R.id.nav_register_work_orders -> {
                navigateToRegisterWorkOrders()
                setTitleToolbar(R.string.title_register_work_order)
            }
            R.id.nav_consult_work_orders -> navigateToConsultWorkOrders()
        }
    }

    /**
     * Navega a la pantalla principal o vista de inicio.
     */
    private fun navigateToHome() {
        // Aquí irá el fragmento Home
    }

    /**
     * Navega al fragmento de registro de órdenes de trabajo.
     */
    private fun navigateToRegisterWorkOrders() {
        val fragment = RegisterWorkOrderFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

    }

    /**
     * Navega al fragmento de consulta de órdenes de trabajo.
     */
    private fun navigateToConsultWorkOrders() {
        // Aquí irá el fragmento de Consulta de Órdenes
    }

    // ──────────────────────────────────────────────
    // CICLO DE VIDA
    // ──────────────────────────────────────────────

    /**
     * Libera recursos y elimina el callback de retroceso al destruir la actividad.
     */
    override fun onDestroy() {
        super.onDestroy()
        backPressedCallback.remove()
    }

}