package pe.kabj.app_movil_kabj.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_session"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_ID_EMPLOYEE = "id_employee"
        private const val KEY_NAMES = "names"
        private const val KEY_SURNAMES = "surnames"
        private const val KEY_GENDER = "gender"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_ACTIVE = "active"
        private const val KEY_PERMISSIONS = "permissions"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit { putString(KEY_AUTH_TOKEN, token) }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    // Datos del usuario
    fun saveUserInfo(
        idEmployee: Long,
        names: String,
        surnames: String,
        gender: String,
        email: String?,
        phone: String?,
        active: Boolean,
        permissions: List<String>
    ) {
        prefs.edit {
            putLong(KEY_ID_EMPLOYEE, idEmployee)
            putString(KEY_NAMES, names)
            putString(KEY_SURNAMES, surnames)
            putString(KEY_GENDER, gender)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putBoolean(KEY_ACTIVE, active)
            putStringSet(KEY_PERMISSIONS, permissions.toSet())
        }
    }

    // Métodos individuales para obtener cada dato
    fun fetchIdEmployee(): Long {
        return prefs.getLong(KEY_ID_EMPLOYEE, -1)
    }

    fun fetchNames(): String? {
        return prefs.getString(KEY_NAMES, null)
    }

    fun fetchSurnames(): String? {
        return prefs.getString(KEY_SURNAMES, null)
    }

    fun fetchGender(): String? {
        return prefs.getString(KEY_GENDER, null)
    }

    fun fetchEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun fetchPhone(): String? {
        return prefs.getString(KEY_PHONE, null)
    }

    fun fetchActive(): Boolean {
        return prefs.getBoolean(KEY_ACTIVE, false)
    }

    fun fetchPermissions(): List<String> {
        return prefs.getStringSet(KEY_PERMISSIONS, emptySet())?.toList() ?: emptyList()
    }

    fun hasPermission(permission: String): Boolean {
        return fetchPermissions().any { it == permission }
    }

    fun clearSession() {
        prefs.edit { clear() }
    }
}
