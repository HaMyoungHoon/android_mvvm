package mhha.sample.android_mvvm.views.dialog.loading

import android.app.Application
import android.content.Context
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseDialog
import mhha.sample.android_mvvm.databinding.LoadingDialogBinding

class LoadingDialog(context: Context, val message: String = ""): FBaseDialog<LoadingDialogBinding, LoadingDialogVM>(context) {
    override var layoutId = R.layout.loading_dialog
    override val dataContext: LoadingDialogVM by lazy {
        LoadingDialogVM(multiDexApplication)
    }

    override fun onCreateAfter() {
        binding?.dataContext = dataContext
        setCancelable(false)
        dataContext.message.value = message
    }
}