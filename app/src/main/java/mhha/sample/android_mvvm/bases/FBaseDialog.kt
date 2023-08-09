package mhha.sample.android_mvvm.bases

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.multidex.MultiDexApplication
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.services.FUIStateService

abstract class FBaseDialog<T1: ViewDataBinding, T2: FBaseViewModel>(private val context: Context): Dialog(context) {
    protected abstract var layoutId: Int
    protected var binding: T1? = null
    protected abstract val dataContext: T2
    private var _context: Context? = null
    val contextBuff get() = _context
    val multiDexApplication by lazy {
        context.applicationContext as MultiDexApplication
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, layoutId, null, false)
        binding?.lifecycleOwner = (context as? LifecycleOwner)
        setContentView(layoutId)
        onCreateAfter()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        _context = window?.context
        onAfterAttach()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _context = null
        binding = null
        onAfterDetach()
    }
    override fun dismiss() {
        super.dismiss()
        _context = null
        binding = null
    }
    open fun onCreateAfter() { }
    open fun onAfterAttach() { }
    open fun onAfterDetach() { }

    protected fun getResString(resId: Int): String {
        if (_context == null) {
            return ""
        }
        return _context!!.getString(resId)
    }
    protected fun getResColor(resId: Int): Int {
        if (_context == null) {
            return 0
        }
        return _context!!.getColor(resId)
    }
}