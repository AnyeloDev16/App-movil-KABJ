package pe.kabj.app_movil_kabj.model.dto.activity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityResponse (

    @SerializedName("id_activity")
    val idActivity: Long,

    @SerializedName("description")
    val description: String,

    @SerializedName("active")
    val active: Boolean

) : Parcelable