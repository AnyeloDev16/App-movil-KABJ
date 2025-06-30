package pe.kabj.app_movil_kabj.presentation.activities

import android.content.Intent
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.data.local.SessionManager
import pe.kabj.app_movil_kabj.databinding.ActivityMainBinding
import pe.kabj.app_movil_kabj.presentation.fragments.AssignWorkOrderFragment
import pe.kabj.app_movil_kabj.presentation.fragments.ConsultWorkOrderFragment
import pe.kabj.app_movil_kabj.presentation.fragments.ForemanWorkOrderListFragment
import pe.kabj.app_movil_kabj.presentation.fragments.HomeFragment
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

    //Components
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigation: NavigationView
    private lateinit var header: View
    private lateinit var fragmentContainer: FrameLayout

    //Variables
    private lateinit var sessionManager: SessionManager
    private lateinit var fullUsername: String

    private val backNavigableFragments = setOf(
        "WorkOrderDetail",
        "WorkOrderDetailForeman"
    )

    /**
     * Callback personalizado para manejar el comportamiento del botón "Atrás".
     * Si el menú lateral está abierto, lo cierra. Si no, delega el comportamiento por defecto.
     */
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else if (supportFragmentManager.backStackEntryCount > 0) {
                // Saber qué fragmento está en la cima de la pila
                val entry = supportFragmentManager.getBackStackEntryAt(
                    supportFragmentManager.backStackEntryCount - 1
                )

                if (entry.name in backNavigableFragments) {
                    // El fragment sí permite retroceder → hacemos popBackStack
                    supportFragmentManager.popBackStack()
                } else {
                    // El fragment actual NO permite retroceso → no hacemos nada
                }
            } else {
                // No hay back stack → no hacer nada (o salir app si quieres)
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
        sessionManager = SessionManager(this)

        setupComponents()
        configureSystemBars()
        setupToolbar()
        setupNavigationDrawer()
        setupBackPressedHandling()

        fullUsername = sessionManager.fetchNames()!! + " " + sessionManager.fetchSurnames()!!
        header.findViewById<TextView>(R.id.tv_full_username).text = fullUsername

        navigation.setCheckedItem(R.id.nav_home)
        navigateToHome()
        setTitleToolbar(R.string.title_home)

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
            val isLogout = menuItem.itemId == R.id.nav_logout

            handleNavigationItemSelected(menuItem.itemId)

            if (!isLogout) {
                menuItem.isChecked = true
                drawerLayout.closeDrawers()
            }

            !isLogout
        }


    }

    /**
     * Registra el comportamiento personalizado del botón físico/gestual "Atrás".
     */
    private fun setupBackPressedHandling() {
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun setTitleToolbar(titleId: Int) {
        supportActionBar?.title = getString(titleId)
    }

    // ──────────────────────────────────────────────
    // NAVEGACIÓN ENTRE VISTAS
    // ──────────────────────────────────────────────

    /**
     * Maneja la navegación según el ítem seleccionado del menú lateral.
     */
    private fun handleNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> {
                navigateToHome()
                setTitleToolbar(R.string.title_home)
            }
            R.id.nav_register_work_orders -> {
                navigateToRegisterWorkOrders()
                setTitleToolbar(R.string.title_register_work_order)
            }
            R.id.nav_consult_work_orders -> {
                navigateToConsultWorkOrders()
                setTitleToolbar(R.string.title_consult_work_order)
            }
            R.id.nav_assign_work_orders -> {
                navigateToAssignWorkOrders()
                setTitleToolbar(R.string.title_assign_work_order)
            }
            R.id.nav_list_work_orders -> {
                navigateToForemanWorkOrderList()
                setTitleToolbar(R.string.title_foreman_work_order_list)
            }
            R.id.nav_logout -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Está seguro que desea cerrar sesión?")
                    .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("Cerrar sesión") { dialog, _ ->
                        // Navegar a LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // Cierra el MainActivity
                    }
                    .show()
            }
        }
    }

    /**
     * Navega a la pantalla principal o vista de inicio.
     */
    private fun navigateToHome() {
        val fragment = HomeFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
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
        val fragment = ConsultWorkOrderFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Navega al fragmento de asignacion de órdenes de trabajo.
     */
    private fun navigateToAssignWorkOrders() {
        val fragment = AssignWorkOrderFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Navega al fragmento de listas de órdenes de trabajo asignados al capataz.
     */
    private fun navigateToForemanWorkOrderList() {
        val fragment = ForemanWorkOrderListFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
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