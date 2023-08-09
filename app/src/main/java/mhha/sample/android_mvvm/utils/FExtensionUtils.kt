package mhha.sample.android_mvvm.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import mhha.sample.android_mvvm.bases.FConstants
import mhha.sample.android_mvvm.models.fDate.FDateTime
import mhha.sample.android_mvvm.models.fDate.FLocalize
import java.util.Locale
import java.util.UUID

object FExtensionUtils {
    fun sharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(FConstants.SHARED_FILE_NAME, Context.MODE_PRIVATE)
    fun cryptoSharedPreferences(context: Context) = EncryptedSharedPreferences.create(
        context,
        FConstants.SHARED_CRYPT_FILE_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun dpToPx(context: Context, dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    fun dpToPx(context: Context, dp: Int) = FExtensionUtils.dpToPx(context, dp.toFloat())

    fun getLocalize() = FLocalize.parseThis(Locale.getDefault().language)
    fun getToday() = FDateTime().setLocalize(getLocalize()).setThis(System.currentTimeMillis())
    fun getTodayString() = getToday().toString("yyyy-MM-dd")
    fun getTodayDateTimeString() = getToday().toString("yyyy-MM-dd hh:mm:ss")

    fun getUUID() = UUID.randomUUID().toString()

    fun getNumberSuffixes(data: Long): String {
        val suffixes = listOf("", "k", "m", "b", "t")
        var value = data.toDouble()
        var suffixIndex = 0
        while (value >= 1000 && suffixIndex < suffixes.size -1) {
            ++suffixIndex
            value /= 1000
        }
        if (suffixIndex == 0) {
            return "%.0F".format(value)
        }
        return "${"%.1f".format(value)}${suffixes[suffixIndex]}"
    }
    fun getNumberSuffixes(data: Int) = FExtensionUtils.getNumberSuffixes(data.toLong())

    fun getDurationToTime(data: Long): String {
        val buff = data / 1000
        if (buff < 0L) {
            return ""
        }
        if (buff == 0L) {
            return "0:00"
        }
        val hours = buff / 3600
        val minutes = (buff % 3600) / 60
        val seconds = buff % 60
        return if (hours > 0L) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    enum class MediaResType(val index: Int) {
        UNKNOWN(-1),
        URI(0),
        URL(1);
        companion object {
            fun fromIndex(data: Int) = MediaResType.values().firstOrNull { it.index == data } ?: MediaResType.UNKNOWN
            fun fromIndex(data: String): MediaResType {
                val index = try {
                    data.toInt()
                } catch (_: Exception) {
                    return MediaResType.UNKNOWN
                }
                return fromIndex(index)
            }
        }
    }
    enum class MediaFileType(val index: Int) {
        UNKNOWN(-1),
        IMAGE(0),
        VIDEO(1);
        companion object {
            fun fromIndex(data: Int) = MediaFileType.values().firstOrNull { it.index == data } ?: IMAGE
        }
    }
}