package pe.kabj.app_movil_kabj.model.dto

import com.google.gson.annotations.SerializedName

data class Page(

    @SerializedName("size")
    val size: Long,

    @SerializedName("number")
    val number: Long,

    @SerializedName("total_elements")
    val totalElements: Long,

    @SerializedName("total_pages")
    val totalPages: Long

)
