package mhha.sample.android_mvvm.models.command

import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.interfaces.command.IEventListener
import java.util.concurrent.atomic.AtomicBoolean

class RelayCommand(private val _execute: (params: Any?) -> Unit, private val _canExecute: ((params: Any?) -> Boolean)? = null): ICommand {
    private var _lastClickedTime: Long = 0L
    private val _isEnabled = AtomicBoolean(true)
    override var clickIntervalMillis = 250
    init {
        _isEnabled.compareAndSet(true, _canExecute?.invoke(null) ?: true)
        isSafe()
    }
    override val isEnabled: AtomicBoolean get() = _isEnabled
    override fun execute(params: Any?) {
        if (!isSafe()) {
            return
        }
        if (_canExecute?.invoke(params) != false && getEnable()) {
            setEnable(false)
            _execute(params)
            event?.onEvent(params)
        }
        setEnable(_canExecute?.invoke(params) ?: true)
    }
    override fun notifyCanExecuteChanged() {
    }

    private fun setEnable(data: Boolean) {
        _isEnabled.compareAndSet(true, data)
    }
    private fun getEnable(): Boolean {
        return _isEnabled.get()
    }
    override var event: IEventListener? = null
    override var asyncEvent: IAsyncEventListener? = null
    fun addEventListener(listener: IEventListener) {
        event = listener
    }
    fun removeEventListener() {
        event = null
    }
    fun setClickInterval(millis: Int) {
        if (millis in 100..10000) {
            clickIntervalMillis = millis
        }
    }
    private fun isSafe(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - _lastClickedTime > clickIntervalMillis) {
            _lastClickedTime = currentTime
            return true
        }
        return false
    }

    fun updateCanExecute(params: Any?) {
        _isEnabled.compareAndSet(true, _canExecute?.invoke(params) ?: true)
        notifyCanExecuteChanged()
    }
}