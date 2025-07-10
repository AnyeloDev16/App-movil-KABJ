package pe.kabj.app_movil_kabj.model.dto.workorder

import com.google.gson.annotations.SerializedName
import pe.kabj.app_movil_kabj.presentation.enums.WorkOrderState

data class WorkOrderForemanResponse (

    @SerializedName("id_work_order")
    val idWorkOrder: Long,

    @SerializedName("number_work_order")
    val numberWorkOrder: Long,

    @SerializedName("activity_description")
    val activityDescription: String,

    @SerializedName("state")
    val state: WorkOrderState,

    @SerializedName("start_date")
    val startDate: String

)