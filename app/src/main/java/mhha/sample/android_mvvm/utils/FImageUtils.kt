package mhha.sample.android_mvvm.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import mhha.sample.android_mvvm.bases.FConstants
import okhttp3.MediaType.Companion.toMediaType
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

object FImageUtils {
    fun fileDelete(context: Context, uri: Uri) {
        if (ableDeleteFile(context, uri)) {
            context.contentResolver.delete(uri, null, null)
        }
    }
    fun ableDeleteFile(context: Context, uri: Uri) = try {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use {
            if (it.moveToFirst()) {
                val filePath = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                val file = File(filePath)
                if (file.exists()) {
                    val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    val isWritable = fileDescriptor.fileDescriptor.valid()
                    fileDescriptor.close()
                    return isWritable
                } else {
                    return false
                }
            } else {
                return true
            }
        }
        false
    } catch (_: Exception) {
        false
    }
    fun isLocalFile(context: Context, fileUri: Uri): Boolean {
        val scheme = fileUri.scheme
        return scheme == "file" || scheme == "context" && ContentResolver.SCHEME_FILE == context.contentResolver.getType(fileUri)
    }
    fun getFileMediaType(file: File): okhttp3.MediaType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)?.toMediaType() ?: "application/json".toMediaType()

    fun uriCopyToTempFolder(context: Context, file: File, fileName: String): Uri {
        val fileDescriptor = context.contentResolver.openFileDescriptor(file.toUri(), "r") ?: return file.toUri()
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var rootDir = File(pictureDir, FConstants.APP_NAME)
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                rootDir = ContextWrapper(context).getDir("Images", Context.MODE_PRIVATE)
            }
        }

        val copiedFile = File(rootDir, fileName)
        if (!copiedFile.exists()) {
            val outputStream = FileOutputStream(copiedFile)
            inputStream.copyTo(outputStream)
            outputStream.close()
        }
        inputStream.close()
        fileDescriptor.close()
        return copiedFile.toUri()
    }
    fun uriToFile(context: Context, fileUri: Uri, fileName: String): File {
        if (isLocalFile(context, fileUri)) return fileUri.toFile()

        val fileDescriptor = context.contentResolver.openFileDescriptor(fileUri, "r") ?: return fileUri.toFile()
        val inputStream = ByteArrayInputStream(FileInputStream(fileDescriptor.fileDescriptor).readBytes())
        val pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var rootDir = File(pictureDir, FConstants.SHARED_FILE_NAME)
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                rootDir = ContextWrapper(context).getDir("Images", Context.MODE_PRIVATE)
            }
        }

        val splitType = context.contentResolver.getType(fileUri)?.split("/") ?: arrayListOf("image", "jpg")
        var extension = if (splitType.size >= 2) {
            splitType[1]
        } else {
            "jpg"
        }

        var needConverter: Boolean = false
        if (extension.lowercase() == "heic") {
            extension = "jpg"
            needConverter = true
        }

        val file = File(rootDir, "${fileName}.$extension")
        if (!file.exists()) {
            inputStream.mark(inputStream.available())
            if (!imageResize(inputStream, file, needConverter)) {
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)
                outputStream.close()
            }
        }
        inputStream.close()
        fileDescriptor.close()
        return file
    }
    fun createImageFile(context: Context): File {
        val timeStamp: String = FExtensionUtils.getToday().toString("yyyyMMdd_HHmmss")
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "jpg_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
        }
    }
    fun imageResize(inputStream: ByteArrayInputStream, outFile: File, needConverter: Boolean = false): Boolean {
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, this)
                inSampleSize = calcResize(this).roundToInt()
                inJustDecodeBounds = false
            }
            if (needConverter) {
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            inputStream.reset()
            val resize = calcResize(options)
            if (resize <= 1F) {
                return false
            }
            val orientation = getOrientation(inputStream)
            val originBitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: return false
            val resizedBitmap = rotateScaledBitmap(originBitmap, options, resize, orientation)
            val outputStream = FileOutputStream(outFile)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            originBitmap.recycle()
            resizedBitmap.recycle()
            outputStream.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }
    fun rotateScaledBitmap(bitMap: Bitmap, options: BitmapFactory.Options, resize: Float, orientation: Int): Bitmap {
        val resizedBitmap = Bitmap.createScaledBitmap(bitMap, (options.outWidth / resize).roundToInt(), (options.outHeight / resize).roundToInt(), false)
        if (orientation == 0) {
            return resizedBitmap
        }
        val rotatedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.width, resizedBitmap.height, Matrix().apply {
            postRotate(orientation.toFloat())
        }, false)

        return rotatedBitmap
    }
    fun getOrientation(inputStream: ByteArrayInputStream): Int {
        val ret = when (ExifInterface(inputStream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        inputStream.reset()
        return ret
    }
    private fun calcResize(options: BitmapFactory.Options, limitSize: Int = 1290): Float {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1F
        if (height > limitSize && width > limitSize) {
            val heightRatio = (height.toFloat() / limitSize)
            val widthRatio = (width.toFloat() / limitSize)
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    fun scaledImageToBitmap(resources: Resources, drawable: Int, dstWidth: Int, dstHeight: Int, filter: Boolean = false) = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, drawable), dstWidth, dstHeight, filter)
    fun scaledVectorToBitmap(context: Context, drawable: Int, dstWidth: Int, dstHeight: Int): Bitmap {
        val vectorDrawable = ContextCompat.getDrawable(context, drawable)
        val bitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable?.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable?.draw(canvas)
        return bitmap
    }
    fun urlImageLoad(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(inputStream)
        connection.disconnect()
        return bitmap
    }
    fun pathImageLoad(imagePath: String): Bitmap {
        val file = File(imagePath)
        return BitmapFactory.decodeFile(file.absolutePath)
    }
    fun imageLoad(context: Context, drawable: Int): Bitmap {
        val vectorDrawable = ContextCompat.getDrawable(context, drawable)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }
    fun imageLoad(context: Context, imageUri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        return BitmapFactory.decodeStream(inputStream)
    }

}