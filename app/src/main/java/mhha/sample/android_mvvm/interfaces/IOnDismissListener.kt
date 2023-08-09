package mhha.sample.android_mvvm.interfaces

interface IOnDismissListener {
    fun onDismiss()
    operator fun invoke(function: () -> Unit) {

    }
}