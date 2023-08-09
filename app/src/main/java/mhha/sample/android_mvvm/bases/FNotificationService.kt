package mhha.sample.android_mvvm.bases

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import mhha.sample.android_mvvm.MainActivity
import mhha.sample.android_mvvm.R
import org.kodein.di.Kodein
import org.kodein.di.android.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import kotlin.math.min

class FNotificationService(application: Application): Service(), KodeinAware {
    override val kodein: Kodein by kodein(application)
    private val _channelId = FConstants.NOTIFICATION_CHANNEL_ID
    private val _channelName = FConstants.NOTIFICATION_CHANNEL_NAME
    private var _notificationId = 0
    private val _progressNotifyBuilder: MutableList<Pair<Pair<String, Int>, NotificationCompat.Builder>> = mutableListOf()
    private val _notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(application)
    }

    fun sendNotify(context: Context, intent: Intent, content: String, notifyType: NotifyType = NotifyType.DEFAULT) {
        createNotificationChannel(context, notifyType)
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            sendNotification(context, intent, content, notifyType)
        }
    }
    fun sendNotify(context: Context, index: Int, content: String, notifyType: NotifyType = NotifyType.DEFAULT) {
        createNotificationChannel(context, notifyType)
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            sendNotification(context, index, content, notifyType)
        }
    }
    fun makeProgressNotify(context: Context, uuid: String, minValue: Int = 0, maxValue: Int = 0, notifyType: NotifyType = NotifyType.DEFAULT) {
        createNotificationChannel(context, notifyType)
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            sendNotification(context, uuid, minValue, maxValue, notifyType)
        }
    }
    fun progressUpdate(context: Context, uuid: String, minValue: Int = 0, maxValue: Int = 0, isCancel: Boolean = false, notifyType: NotifyType = NotifyType.DEFAULT) {
        val findNotify = _progressNotifyBuilder.find { it.first.first == uuid } ?: return
        val notificationId = findNotify.first.second
        val notification = findNotify.second
        _progressNotifyBuilder.remove(findNotify)
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            updateNotification(context, notificationId, notification, minValue, maxValue, isCancel, notifyType)
        }
    }
    private fun createNotificationChannel(context: Context, notifyType: NotifyType = NotifyType.DEFAULT) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(_channelId, _channelName, NotificationManager.IMPORTANCE_HIGH)
            when (notifyType) {
                NotifyType.DEFAULT -> {
                    channel.setSound(getSoundUri(context), getAudioAttribute())
                    channel.vibrationPattern = getVibrationPattern()
                    channel.enableVibration(true)
                }
                NotifyType.WITHOUT_VIBRATE -> {
                    channel.setSound(getSoundUri(context), getAudioAttribute())
                    channel.enableVibration(false)
                }
                NotifyType.WITHOUT_SOUND -> {
                    channel.vibrationPattern = getVibrationPattern()
                    channel.enableVibration(true)
                }
                NotifyType.WITHOUT_VIBRATE_AND_SOUND -> { channel.enableVibration(false) }
            }
            _notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(context: Context, intent: Intent, content: String, notifyType: NotifyType = NotifyType.DEFAULT) {
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, _channelId)
            .setContentTitle(_channelName)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                when (notifyType) {
                    NotifyType.DEFAULT -> {
                        setSound(getSoundUri(context), AudioManager.STREAM_ALARM)
                        setVibrate(getVibrationPattern())
                        setVibrate(context)
                    }
                    NotifyType.WITHOUT_VIBRATE -> {
                        setSound(getSoundUri(context), AudioManager.STREAM_ALARM)
                    }
                    NotifyType.WITHOUT_SOUND -> {
                        setSilent(true)
                        setVibrate(getVibrationPattern())
                        setVibrate(context)
                    }
                    NotifyType.WITHOUT_VIBRATE_AND_SOUND -> {
                        setSilent(true)
                    }
                }
            }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        _notificationManager.notify(_notificationId++, notification.build())
    }
    private fun sendNotification(context: Context, index: Int, content: String, notifyType: NotifyType = NotifyType.DEFAULT) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtras(Bundle().apply {
                putExtra("alarmType", index)
            })
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, _channelId)
            .setContentTitle(_channelName)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .apply {
                when (notifyType) {
                    NotifyType.DEFAULT -> {
                        setSound(getSoundUri(context), AudioManager.STREAM_ALARM)
                        setVibrate(getVibrationPattern())
                        setVibrate(context)
                    }
                    NotifyType.WITHOUT_VIBRATE -> {
                        setSound(getSoundUri(context), AudioManager.STREAM_ALARM)
                    }
                    NotifyType.WITHOUT_SOUND -> {
                        setSilent(true)
                        setVibrate(getVibrationPattern())
                        setVibrate(context)
                    }
                    NotifyType.WITHOUT_VIBRATE_AND_SOUND -> {
                        setSilent(true)
                    }
                }
            }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        _notificationManager.notify(_notificationId++, notification.build())
    }
    private fun sendNotification(context: Context, uuid: String, minValue: Int = 0, maxValue: Int = 0, notifyType: NotifyType = NotifyType.DEFAULT) {
        val notification = NotificationCompat.Builder(context, _channelId)
            .setContentTitle("do something")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setProgress(maxValue, minValue, maxValue == 0)
            .setAutoCancel(false)
        val notificationId = _notificationId++
        _progressNotifyBuilder.add(Pair(Pair(uuid, notificationId), notification))
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        _notificationManager.notify(notificationId, notification.build())
    }
    fun updateNotification(context: Context, notificationId: Int, notification: NotificationCompat.Builder, minValue: Int = 0, maxValue: Int = 0, isCancel: Boolean = false, notifyType: NotifyType = NotifyType.DEFAULT) {
        if (isCancel) {
            _notificationManager.cancel(notificationId)
            return
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val notifyBuff = notification.apply {
            when (notifyType) {
                NotifyType.DEFAULT -> {
                    setSound(getSoundUri(context), AudioManager.STREAM_ALARM)
                    setVibrate(getVibrationPattern())
                    setVibrate(context)
                }
                NotifyType.WITHOUT_VIBRATE -> {
                    setSound(getSoundUri(context), AudioManager.STREAM_ALARM)
                }
                NotifyType.WITHOUT_SOUND -> {
                    setSilent(true)
                    setVibrate(context)
                }
                NotifyType.WITHOUT_VIBRATE_AND_SOUND -> {
                    setSilent(true)
                }
            }
        }
        notification.setProgress(maxValue, minValue, maxValue == 0)
        _notificationManager.notify(notificationId, notifyBuff.build())
    }

    private fun setVibrate(context: Context) {
        return
        val pattern = getVibrationPattern()
        val amplitude = getAmplitude()
        val repeat = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager ?: return
            val vibrationEffect = VibrationEffect.createWaveform(pattern, amplitude, repeat)
            val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
            vibratorManager.vibrate(combinedVibration)
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitude, repeat))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        }
    }
    private fun getSoundUri(context: Context): Uri {
//        return Uri.parse("file:///android_assets/music/buff_music")
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.buff_music)
    }
    private fun getAudioAttribute(): AudioAttributes {
        return AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
    }
    private fun getVibrationPattern(): LongArray {
        return longArrayOf(100, 150, 150, 150, 200, 200)
    }
    private fun getAmplitude(): IntArray {
        return intArrayOf(50, 50, 50, 100, 50, 150)
    }
    fun offVibrate() {
//        _notificationManager.cancelAll()
    }
    fun offSound() {
//        _notificationManager.cancelAll()
    }

    enum class NotifyType(val index: Int) {
        DEFAULT(1),
        WITHOUT_VIBRATE(2),
        WITHOUT_SOUND(3),
        WITHOUT_VIBRATE_AND_SOUND(4);
        companion object {
            fun fromIndex(index: Int?) = NotifyType.values().firstOrNull { it.index == index }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}