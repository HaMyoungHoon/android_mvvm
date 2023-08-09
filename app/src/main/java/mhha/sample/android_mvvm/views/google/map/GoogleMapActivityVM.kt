package mhha.sample.android_mvvm.views.google.map

import android.app.Application
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import mhha.sample.android_mvvm.bases.FBaseViewModel

class GoogleMapActivityVM(application: MultiDexApplication): FBaseViewModel(application) {
    val circleRadius = MutableStateFlow(100)
    val circleRadiusString = MutableStateFlow("0.1")
    val address = MutableStateFlow("")
    val testText = MutableStateFlow("")
    fun setRange(data: Int?) {
        circleRadius.value = if (data == null) 100 else (data * 100)
        circleRadiusString.value = "${(circleRadius.value.toDouble() / 1000)} km"
    }

    enum class ClickEvent {
        CLOSE,
        SAVE,
    }
}