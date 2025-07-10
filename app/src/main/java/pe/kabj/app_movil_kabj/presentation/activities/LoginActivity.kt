package pe.kabj.app_movil_kabj.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.databinding.ActivityLoginBinding
import pe.kabj.app_movil_kabj.util.extensions.getTrimmedText
import pe.kabj.app_movil_kabj.presentation.viewmodels.LoginViewModel
import androidx.lifecycle.ViewModelProvider
import pe.kabj.app_movil_kabj.model.dto.OperationResult
import pe.kabj.app_movil_kabj.presentation.utils.ModalDialogUtils

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //Components
    private lateinit var layoutTxtUsername: TextInputLayout
    private lateinit var txtUsername: TextInputEditText
    private lateinit var layoutTxtPassword: TextInputLayout
    private lateinit var txtPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var progressOverlay: LinearLayout

    //ViewModel
    private val loginViewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(applicationContext) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemBars()

        setupComponents()
        setupListeners()
        setupObservers()
        animateLogin()

    }

    private fun setupComponents() {
        this.layoutTxtUsername = binding.layoutTxtUsername
        this.txtUsername = binding.txtUsername
        this.layoutTxtPassword = binding.layoutTxtPassword
        this.txtPassword = binding.txtPassword
        this.btnLogin = binding.btnLogin
        this.progressOverlay = binding.progressOverlay
    }

    private fun setupListeners() {
        txtUsername.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                layoutTxtUsername.error = null
            }
        }

        txtPassword.addTextChangedListener {
            if (!it.isNullOrBlank()) {
                layoutTxtPassword.error = null
            }
        }
        btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun setupObservers() {
        loginViewModel.isLoginButtonPressed.observe(this) { isLoginButtonPressed ->
            btnLogin.isEnabled = isLoginButtonPressed.not()
        }

        loginViewModel.isAuthenticating.observe(this) { isLoggingIn ->
            progressOverlay.visibility = if (isLoggingIn)  View.VISIBLE else View.GONE
        }

        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is OperationResult.Success -> {
                    goToMainActivity()
                }
                is OperationResult.Error -> {
                    showLoginError(result)
                }
            }
        }

    }

    private fun handleLogin() {

        val txtUsername: String = txtUsername.getTrimmedText()
        val txtPassword: String = txtPassword.getTrimmedText()

        val isValid : Boolean = loginViewModel.isLoginFormValid(txtUsername, txtPassword)

        if (!isValid) {
            if (txtUsername.isBlank()) {
                layoutTxtUsername.error = getString(R.string.msg_error_username_required)
            } else {
                layoutTxtUsername.error = null
            }

            if (txtPassword.isBlank()) {
                layoutTxtPassword.error = getString(R.string.msg_error_password_required)
            } else {
                layoutTxtPassword.error = null
            }
            return
        }

        loginViewModel.authenticateUser(txtUsername, txtPassword)

    }

    private fun animateLogin() {
        binding.imageLogo.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(100)
            .start()

        binding.cardViewLogin.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(
            WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars()
        )
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun showLoginError(error: OperationResult.Error) {
        ModalDialogUtils.showFailureDialog(this, error.title, error.message)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}