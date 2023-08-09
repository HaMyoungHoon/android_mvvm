package mhha.sample.android_mvvm.bases

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.multidex.MultiDexApplication
import mhha.sample.android_mvvm.services.FUIStateService
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

abstract class FBaseActivity<T1: ViewDataBinding, T2: FBaseViewModel>: AppCompatActivity(), KodeinAware {
    protected abstract var layoutId: Int
    protected var binding: T1? = null
    protected abstract val dataContext: T2
    private val uiStateService: FUIStateService by lazy {
        FUIStateService()
    }
    val multiDexApplication: MultiDexApplication by lazy {
        this.application as MultiDexApplication
    }
    override val kodein: Kodein by closestKodein()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding?.lifecycleOwner = this
        afterOnCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        loading(true)
        binding = null
    }
    open fun afterOnCreate() { }
    protected fun toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
        uiStateService.toast(this, message, duration)
    }
    protected fun loading(dismiss: Boolean = false) {
        uiStateService.loading(this, dismiss)
    }
}