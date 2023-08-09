package mhha.sample.android_mvvm.views.main.home.network

import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import mhha.sample.android_mvvm.bases.FBaseViewModel

class HomeSubNetworkFragmentVM(application: MultiDexApplication): FBaseViewModel(application) {
    val ssidData = MutableStateFlow("")

    enum class ClickEvent {
        WIFI_SSID,
        OPEN_CONNECT,
    }
}