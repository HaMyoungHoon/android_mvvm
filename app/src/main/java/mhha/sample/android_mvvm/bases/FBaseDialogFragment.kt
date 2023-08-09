package mhha.sample.android_mvvm.bases

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.multidex.MultiDexApplication
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.services.FUIStateService

abstract class FBaseDialogFragment<T1: ViewDataBinding, T2: FBaseViewModel>: DialogFragment() {
    protected abstract var layoutId: Int
    protected var binding: T1? = null
    protected abstract val dataContext: T2
    private var _isAttached = false
    private var _context: Context? = null
    val isAttached get() = _isAttached
    val contextBuff get() = _context
    private val uiStateService: FUIStateService by lazy {
        FUIStateService()
    }
    val multiDexApplication by lazy {
        requireActivity().application as MultiDexApplication
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateAfter()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        onBindAfter()
        return binding?.root
    }
    override fun getTheme() = R.style.base_dialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
        _isAttached = true
        onAfterAttach()
    }
    override fun onDetach() {
        _isAttached = false
        super.onDetach()
        _context = null
        onAfterDetach()
    }
    override fun onDestroy() {
        super.onDestroy()
        loading(true)
        binding = null
    }

    open fun onCreateAfter() { }
    open fun onBindAfter() { }
    open fun onAfterAttach() { }
    open fun onAfterDetach() { }

    protected fun toast(message: String?) {
        if (_context == null) {
            return
        }
        uiStateService.toast(_context!!, message)
    }
    protected fun loading(dismiss: Boolean = false) {
        if (_context == null) {
            return
        }
        uiStateService.loading(_context!!, dismiss)
    }
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