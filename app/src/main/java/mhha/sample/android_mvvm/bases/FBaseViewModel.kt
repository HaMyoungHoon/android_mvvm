package mhha.sample.android_mvvm.bases

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.multidex.MultiDexApplication
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.models.command.AsyncRelayCommand
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

abstract class FBaseViewModel(application: MultiDexApplication): AndroidViewModel(application), KodeinAware {
    final override val kodein: Kodein by kodein(application)

    val relayCommand: ICommand = AsyncRelayCommand({})
    fun addEventListener(listener: IAsyncEventListener) {
        (relayCommand as AsyncRelayCommand).addEventListener(listener)
    }
}