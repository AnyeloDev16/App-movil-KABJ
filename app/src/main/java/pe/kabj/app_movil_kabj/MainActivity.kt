package pe.kabj.app_movil_kabj

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
import pe.kabj.app_movil_kabj.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigation: NavigationView
    private lateinit var header: View
    private lateinit var fragmentContainer: FrameLayout

    private lateinit var username: String

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

        navigateToHome()
    }

    // ──────────────────────────────────────────────
    // SETUP Y CONFIGURACIÓN
    // ──────────────────────────────────────────────

    private fun setupComponents() {
        drawerLayout = binding.drawerLayout
        toolbar = binding.toolbar
        navigation = binding.navView
        header = navigation.getHeaderView(0)
        fragmentContainer = binding.fragmentContainer
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
            handleNavigationItemSelected(menuItem.itemId)
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupBackPressedHandling() {
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    // ──────────────────────────────────────────────
    // NAVEGACIÓN
    // ──────────────────────────────────────────────

    private fun handleNavigationItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> navigateToHome()
            R.id.nav_register_work_orders -> navigateToRegisterWorkOrders()
            R.id.nav_consult_work_orders -> navigateToConsultWorkOrders()
        }
    }

    private fun navigateToHome() {
        // Aquí irá el fragmento Home
    }

    private fun navigateToRegisterWorkOrders() {
        // Aquí irá el fragmento de Registro de Órdenes
    }

    private fun navigateToConsultWorkOrders() {
        // Aquí irá el fragmento de Consulta de Órdenes
    }

    // ──────────────────────────────────────────────
    // CICLO DE VIDA
    // ──────────────────────────────────────────────

    override fun onDestroy() {
        super.onDestroy()
        backPressedCallback.remove()
    }
}