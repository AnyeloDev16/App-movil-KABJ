package pe.kabj.app_movil_kabj.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object FileUtils {

    fun createUploadFileData(context: Context, uri: Uri): UploadFileData {
        val fileName = extractFileName(context, uri)
        val tempFile = createFileFromUri(context, uri, fileName)

        val mediaTypeStr = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val mediaType = MediaType.parse(mediaTypeStr)

        val requestFile = RequestBody.create(
            mediaType,
            tempFile
        )

        val multipart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

        return UploadFileData(
            fileName = tempFile.name,
            fileSizeFormatted = formatFileSize(tempFile.length()),
            multipartBody = multipart,
            tempFile = tempFile
        )
    }

    private fun createFileFromUri(context: Context, uri: Uri, fileName: String): File {
        val tempFile = File(context.cacheDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun extractFileName(context: Context, uri: Uri): String {
        var name = "archivo.xlsx"
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex != -1) {
                name = it.getString(nameIndex) ?: name
            }
        }
        return name
    }

    private fun formatFileSize(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 0 -> "Tamaño desconocido"
            sizeInBytes < 1024 -> "$sizeInBytes B"
            sizeInBytes < 1024 * 1024 -> String.format("%.1f kB", sizeInBytes / 1024.0)
            else -> String.format("%.1f MB", sizeInBytes / (1024.0 * 1024))
        }
    }
}
