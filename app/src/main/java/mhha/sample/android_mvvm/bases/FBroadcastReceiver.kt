package mhha.sample.android_mvvm.bases

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.Calendar

class FBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val kodein = (context.applicationContext as? KodeinAware)?.kodein ?: return
        val fNotificationService: FNotificationService by kodein.instance(FNotificationService::class)
        val content = intent?.getStringExtra("content") ?: ""
        val alarmType = intent?.getIntExtra("alarmType", 0) ?: 0
        fNotificationService.sendNotify(context, alarmType, content, FNotificationService.NotifyType.DEFAULT)
    }

    fun setAlarm(context: Context, content: String, afterSec: Int = 1) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val broadcastIntent = Intent(context, FBroadcastReceiver::class.java).apply {
            putExtra("content", content)
            putExtra("alarmType", FNotificationService.NotifyType.DEFAULT.index)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            broadcastIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val triggerTime = System.currentTimeMillis() + 1000 * afterSec
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }
}