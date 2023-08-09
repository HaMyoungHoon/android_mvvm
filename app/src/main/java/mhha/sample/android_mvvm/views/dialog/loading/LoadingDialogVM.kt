package mhha.sample.android_mvvm.views.dialog.loading

import android.app.Application
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import mhha.sample.android_mvvm.bases.FBaseViewModel

class LoadingDialogVM(application: MultiDexApplication): FBaseViewModel(application) {
    val message = MutableStateFlow("")
}