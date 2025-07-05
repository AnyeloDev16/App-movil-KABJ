package pe.kabj.app_movil_kabj.data.dto.supplynumber

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SupplyNumberResponse (

    @SerializedName("id_supply_number")
    val idSupplyNumber: Long,

    @SerializedName("number_nis")
    val numberNis: Long,

    @SerializedName("address")
    val address: String,

    @SerializedName("locality")
    val locality: String,

    @SerializedName("district")
    val district: String,

    @SerializedName("sector")
    val sector: Long,

    @SerializedName("active")
    val active: Boolean

) : Parcelable