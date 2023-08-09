package mhha.sample.android_mvvm.views.video

import android.app.Application
import android.net.Uri
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import mhha.sample.android_mvvm.bases.FBaseViewModel

class VideoEditActivityVM(application: MultiDexApplication): FBaseViewModel(application) {

    var videoPath = MutableStateFlow<Uri?>(null)

    enum class ClickEvent {
        CLOSE,
        SAVE
    }
}