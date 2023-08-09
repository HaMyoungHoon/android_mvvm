package mhha.sample.android_mvvm.models.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.interfaces.command.IEventListener
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AsyncRelayCommand(private val _execute: suspend CoroutineScope.(params: Any?) -> Unit, private var _canExecute: ((params: Any?) -> Boolean)? = null): ICommand {
    private var _lastClickedTime: Long = 0L
    private val _isEnabled = AtomicBoolean(true)
    override var clickIntervalMillis = 250
    init {
        _isEnabled.compareAndSet(true, true)
        isSafe()
    }
    override val isEnabled: AtomicBoolean get() = _isEnabled
    override fun execute(params: Any?) {
        if (!isSafe()) {
            return
        }
        if (_canExecute?.invoke(params) != false && getEnable()) {
            setEnable(false)
            CoroutineScope(Dispatchers.Main).launch {
                val context = if (this.coroutineContext.isActive) Dispatchers.Main else Dispatchers.IO
                withContext(context) {
                    suspendCoroutine<Unit> { continuation ->
                        launch {
                            try {
                                _execute(params)
                                asyncEvent?.onEvent(params)
                                continuation.resume(setEnable(true))
                            } catch (e: Exception) {
                                continuation.resumeWithException(e)
                                setEnable(true)
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setEnable(data: Boolean) {
        _isEnabled.compareAndSet(!data, data)
    }
    private fun getEnable(): Boolean {
        return _isEnabled.get()
    }

    override fun notifyCanExecuteChanged() {
    }

    override var event: IEventListener? = null
    override var asyncEvent: IAsyncEventListener? = null
    fun addEventListener(listener: IAsyncEventListener) {
        asyncEvent = listener
    }
    fun removeEventListener() {
        asyncEvent = null
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