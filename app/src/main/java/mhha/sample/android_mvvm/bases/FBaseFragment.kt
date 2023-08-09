package mhha.sample.android_mvvm.bases

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDexApplication
import mhha.sample.android_mvvm.services.FUIStateService

abstract class FBaseFragment<T1: ViewDataBinding, T2: FBaseViewModel>: Fragment() {
    protected abstract var layoutId: Int
    protected var binding: T1? = null
    protected abstract val dataContext: T2
    private var _needTokenRefresh = true
    private var _isAttached = false
    private var _context: Context? = null
    val initAble get () = !_needTokenRefresh
    val isAttached get() = _isAttached
    val contextBuff get() = _context
    private val uiStateService: FUIStateService by lazy {
        FUIStateService()
    }
    val multiDexApplication: MultiDexApplication by lazy {
        requireActivity().application as MultiDexApplication
    }
    private var singlePermissionResult: ActivityResultLauncher<String>? = null
    private var multiPermissionResult: ActivityResultLauncher<Array<String>>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding?.lifecycleOwner = viewLifecycleOwner
        _needTokenRefresh = false
//        _needTokenRefresh = getToken()
        initPermissionResult()
        onBindAfter()
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreatedAfter()
    }
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
        singlePermissionResult = null
        multiPermissionResult = null
        super.onDestroy()
        loading(true)
        binding = null
    }
    open fun onBindAfter() { }
    open fun onViewCreatedAfter() { }
    open fun onAfterAttach() { }
    open fun onAfterDetach() { }

    private fun initPermissionResult() {
        singlePermissionResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) { x ->
            onSinglePermission(x)
        }
        multiPermissionResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { x ->
            onMultiPermission(x)
        }
    }
    open fun onSinglePermission(data: Boolean) { }
    open fun onMultiPermission(data: Map<String, Boolean>) { }

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

    protected fun shouldShowRequestPermissionRationale(permissions: Array<String>) = permissions.any {
        shouldShowRequestPermissionRationale(it)
    }
    protected fun hasPermissionsGranted(permissions: Array<String>): Boolean {
        val context = contextBuff ?: return false
        return permissions.none {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }
    protected fun requestSinglePermissions(permission: String) {
        if (!shouldShowRequestPermissionRationale(permission)) {
            singlePermissionResult?.launch(permission)
        }
    }
    protected fun requestMultiPermissions(permissions: Array<String>) {
        if (!shouldShowRequestPermissionRationale(permissions)) {
            multiPermissionResult?.launch(permissions)
        }
    }
}