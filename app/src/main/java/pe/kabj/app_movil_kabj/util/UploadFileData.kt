package pe.kabj.app_movil_kabj.util

import okhttp3.MultipartBody
import java.io.File

data class UploadFileData(
    val fileName: String,
    val fileSizeFormatted: String,
    val multipartBody: MultipartBody.Part,
    val tempFile: File
)
