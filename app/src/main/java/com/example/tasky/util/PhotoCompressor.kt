package com.example.tasky.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class PhotoCompressor(
    private val context: Context
) {
    suspend fun compressPhoto(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            return@withContext bitmap?.let { bmp ->
                compressBitmapToUnder1MB(bmp)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e


            e.printStackTrace()
            return@withContext null
        }
    }

    private fun compressBitmapToUnder1MB(bitmap: Bitmap, maxBytes: Int = 1_000_000): ByteArray? {
        var quality = 100
        var compressedData: ByteArray

        do {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            compressedData = outputStream.toByteArray()
            quality -= 5  // Reduce quality incrementally

            outputStream.close()
        } while (compressedData.size > maxBytes && quality > 5)

        return if (compressedData.size <= maxBytes) compressedData else null
    }
}