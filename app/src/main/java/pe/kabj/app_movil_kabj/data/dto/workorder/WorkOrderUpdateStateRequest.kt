package pe.kabj.app_movil_kabj.data.dto.workorder

import com.google.gson.annotations.SerializedName
import pe.kabj.app_movil_kabj.presentation.enums.WorkOrderState

data class WorkOrderUpdateStateRequest (

    @SerializedName("number_ot")
    val numberOT: Long,

    @SerializedName("state")
    val state: WorkOrderState

)