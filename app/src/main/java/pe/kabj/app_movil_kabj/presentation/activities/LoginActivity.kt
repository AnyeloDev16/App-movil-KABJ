package pe.kabj.app_movil_kabj.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pe.kabj.app_movil_kabj.R
import pe.kabj.app_movil_kabj.databinding.ActivityLoginBinding
import pe.kabj.app_movil_kabj.extensions.getTrimmedText
import pe.kabj.app_movil_kabj.extensions.validateNotEmpty

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //Components
    private lateinit var layoutTxtUsername: TextInputLayout
    private lateinit var txtUsername: TextInputEditText
    private lateinit var layoutTxtPassword: TextInputLayout
    private lateinit var txtPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemBars()

        setupComponents()
        setupListeners()
        animateLogin()

    }

    private fun setupComponents() {
        this.layoutTxtUsername = binding.layoutTxtUsername
        this.txtUsername = binding.txtUsername
        this.layoutTxtPassword = binding.layoutTxtPassword
        this.txtPassword = binding.txtPassword
        this.btnLogin = binding.btnLogin
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

    private fun handleLogin() {
        val isUsernameValid = layoutTxtUsername.validateNotEmpty(getString(R.string.msg_error_username_required))
        val isPasswordValid = layoutTxtPassword.validateNotEmpty(getString(R.string.msg_error_password_required))

        if (isUsernameValid && isPasswordValid) {
            val username = txtUsername.getTrimmedText()
            val password = txtPassword.getTrimmedText()

            showToast(getString(R.string.msg_login_success) + " $username")

            val intent = Intent(this, MainActivity::class.java)

            intent.putExtra("username", username)

            startActivity(intent)
            finish()

        }
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


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

}