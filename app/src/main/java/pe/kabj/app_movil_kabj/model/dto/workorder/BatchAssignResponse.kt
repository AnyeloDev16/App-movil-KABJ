package pe.kabj.app_movil_kabj.model.dto.workorder

import com.google.gson.annotations.SerializedName

data class BatchAssignResponse (

    @SerializedName("processed_count")
    val processedCount: Long,

    @SerializedName("not_found_ids")
    val notFoundIds: List<Long>

)