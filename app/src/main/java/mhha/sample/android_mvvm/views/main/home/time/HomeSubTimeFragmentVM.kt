package mhha.sample.android_mvvm.views.main.home.time

import androidx.multidex.MultiDexApplication
import mhha.sample.android_mvvm.bases.FBaseViewModel

class HomeSubTimeFragmentVM(application: MultiDexApplication): FBaseViewModel(application) {

    enum class ClickEvent {
        TIME,
        CALENDAR,
    }
}