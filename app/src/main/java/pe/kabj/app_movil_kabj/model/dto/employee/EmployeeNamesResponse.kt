package pe.kabj.app_movil_kabj.model.dto.employee

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmployeeNamesResponse (

    @SerializedName("names")
    val names: String,

    @SerializedName("surnames")
    val surnames: String

) : Parcelable {
    fun getFullName(): String {
        return "$names $surnames"
    }
}