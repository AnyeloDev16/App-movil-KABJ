package pe.kabj.app_movil_kabj.model.dto.workorder

import com.google.gson.annotations.SerializedName

data class WorkOrderAssignRequest (

    @SerializedName("number_ot")
    val numberOt: Long

)