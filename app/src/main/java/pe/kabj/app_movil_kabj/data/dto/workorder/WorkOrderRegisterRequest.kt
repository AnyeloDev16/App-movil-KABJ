package pe.kabj.app_movil_kabj.data.dto.workorder

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime

data class WorkOrderRegisterRequest(
    @SerializedName("number_ot")
    val numberOt: Long,

    @SerializedName("id_activity")
    val idActivity: Long,

    @SerializedName("id_sub_activity")
    val idSubActivity: Long,

    @SerializedName("quantity")
    val quantity: BigDecimal,

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

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("work_order_type")
    val workOrderType: String,

    @SerializedName("work_order_state")
    val workOrderState: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("observation")
    val observation: String
)