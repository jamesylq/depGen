package com.example.depgen.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.depgen.ctxt

fun Uri.uriToBase64(context: Context = ctxt, quality: Int = 1): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(this)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBytes = outputStream.toByteArray()
        outputStream.close()

        Base64.encodeToString(compressedBytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.toImageBitmap(): ImageBitmap? {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "camera_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val uri = context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )

    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }

    return uri ?: Uri.EMPTY
}

fun ImageBitmap.toSquare(): ImageBitmap {
    val size = minOf(this.width, this.height)
    return this.asAndroidBitmap()
        .let { Bitmap.createBitmap(it, 0, 0, size, size) }
        .asImageBitmap()
}
