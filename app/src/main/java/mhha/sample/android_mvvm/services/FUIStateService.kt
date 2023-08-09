package mhha.sample.android_mvvm.services

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.databinding.ToastDialogBinding
import mhha.sample.android_mvvm.utils.FCoroutineUtil
import mhha.sample.android_mvvm.views.dialog.loading.LoadingDialog

class FUIStateService {
    private var loadingDialog: LoadingDialog? = null
    fun toast(context: Context, message: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (message.isNullOrEmpty()) {
            return
        }
        val motherContext = context.applicationContext
        FCoroutineUtil.coroutineScope({
            val inflater = LayoutInflater.from(motherContext)
            val binding: ToastDialogBinding = DataBindingUtil.inflate(inflater, R.layout.toast_dialog, null, false)
            binding.tvSample.text = message
            Toast(motherContext).apply {
                setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, 0)
                duration
                view = binding.root
            }
        }, { it.show() })
    }
    fun loading(context: Context, isDismiss: Boolean = false) {
        if (isDismiss) {
            if (loadingDialog?.isShowing == true) {
                loadingDialog?.dismiss()
            }
            return
        }
        if (loadingDialog?.isShowing == true) {
            return
        }
        loadingDialog = LoadingDialog(context)
        try {
            loadingDialog?.show()
        } catch (e: Exception) {
            Log.d("loadingDialog", e.message ?: "")
        }
    }
}