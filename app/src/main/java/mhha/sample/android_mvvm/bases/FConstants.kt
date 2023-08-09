package mhha.sample.android_mvvm.bases

import android.Manifest

object FConstants {
    const val APP_NAME = "androidMvvm"

    const val SHARED_FILE_NAME = "AndroidMvvm"
    const val SHARED_CRYPT_FILE_NAME = "AndroidMvvmCrypt"

    const val NOTIFICATION_CHANNEL_ID = "notify_channel_android_mvvm"
    const val NOTIFICATION_CHANNEL_NAME = "NotifyChannelAndroidMvvm"

    const val REST_API_DEBUG_URL = "https://localhost:9090"
    const val REST_API_URL = "https://localhost:8080"

    val LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    val ALARM_PERMISSION = arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM)

    enum class Permit(val index: Int) {
        LOCATION(1),
        ALARM(2),
        LOCATION_WIFI(3),
    }
}