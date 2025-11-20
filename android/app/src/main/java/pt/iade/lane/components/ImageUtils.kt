package pt.iade.lane.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream


fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBytes = inputStream.readBytes()
        val originalBitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size) ?: return null
        val rotatedBitmap = correctBitmapRotation(context, uri, originalBitmap)
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val bytes = outputStream.toByteArray()
        Base64.encodeToString(bytes, Base64.DEFAULT)

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
private fun correctBitmapRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
    return try {
        val input: InputStream? = context.contentResolver.openInputStream(uri)
        val exif = ExifInterface(input!!)

        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotationDegrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        if (rotationDegrees != 0f) {
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees)

            Bitmap.createBitmap(
                bitmap,
                0, 0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            bitmap
        }

    } catch (_: Exception) {
        bitmap
    }
}
fun decodeBase64ToBitmapSafe(
    base64: String?,
    reqWidth: Int = 1080,
    reqHeight: Int = 1080
): Bitmap? {
    if (base64.isNullOrBlank()) return null

    return runCatching {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        val (width, height) = options.outWidth to options.outHeight
        if (width <= 0 || height <= 0) return null
        options.inSampleSize = calculateInSampleSize(width, height, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }.getOrNull()
}
private fun calculateInSampleSize(
    width: Int,
    height: Int,
    reqWidth: Int,
    reqHeight: Int
): Int {
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight &&
            (halfWidth / inSampleSize) >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
