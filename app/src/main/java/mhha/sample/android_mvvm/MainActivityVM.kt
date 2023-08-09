package mhha.sample.android_mvvm

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import mhha.sample.android_mvvm.bases.FBaseViewModel
import mhha.sample.android_mvvm.bases.FBroadcastReceiver
import mhha.sample.android_mvvm.bases.FNotificationService
import org.kodein.di.generic.instance

class MainActivityVM(application: MultiDexApplication): FBaseViewModel(application) {
//    private val broadcastReceiver: FBroadcastReceiver by kodein.instance(FBroadcastReceiver::class)
    private val fNotificationString: FNotificationService by kodein.instance(FNotificationService::class)
    val menuItems = MutableStateFlow(mutableListOf<MainMenuListModel>())

    fun setAlarm(context: Context, content: String, afterSec: Int) {
        FBroadcastReceiver().setAlarm(context, content, afterSec)
    }
    fun offSound() {
        fNotificationString.offSound()
    }
    fun offVibrate() {
        fNotificationString.offVibrate()
    }

    enum class ClickEvent {

    }
}