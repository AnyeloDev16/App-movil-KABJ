package pe.kabj.app_movil_kabj.model.dto.workorder

import com.google.gson.annotations.SerializedName

data class BatchResponse (

    @SerializedName("processed_count")
    val processedCount: Long,

    @SerializedName("error_general")
    val errorGeneral: List<Long>,

    @SerializedName("duplicate_number_work_order")
    val duplicateNumberWorkOrder: List<Long>,

    @SerializedName("unrelated_activities")
    val unrelatedActivities: List<Long>

)