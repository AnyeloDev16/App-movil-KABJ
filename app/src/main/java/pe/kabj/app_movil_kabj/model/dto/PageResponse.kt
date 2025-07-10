package pe.kabj.app_movil_kabj.model.dto

import com.google.gson.annotations.SerializedName

data class PageResponse<T> (

    @SerializedName("content")
    val content: List<T>,

    @SerializedName("page")
    val page: Page

)