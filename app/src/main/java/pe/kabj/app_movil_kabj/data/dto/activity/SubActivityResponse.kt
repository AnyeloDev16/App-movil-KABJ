package pe.kabj.app_movil_kabj.data.dto.activity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubActivityResponse (

    @SerializedName("id_sub_activity")
    val idSubActivity: Long,

    @SerializedName("description")
    val description: String,

    @SerializedName("active")
    val active: Boolean

) : Parcelable