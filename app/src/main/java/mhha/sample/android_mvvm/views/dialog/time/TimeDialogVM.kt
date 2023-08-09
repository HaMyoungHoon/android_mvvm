package mhha.sample.android_mvvm.views.dialog.time

import android.app.Application
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import mhha.sample.android_mvvm.bases.FBaseViewModel

class TimeDialogVM(application: MultiDexApplication): FBaseViewModel(application) {
    val hour = MutableStateFlow(0)
    val minute = MutableStateFlow(0)
    enum class ClickEvent {
        CLOSE,
        CANCEL,
        SAVE
    }
}