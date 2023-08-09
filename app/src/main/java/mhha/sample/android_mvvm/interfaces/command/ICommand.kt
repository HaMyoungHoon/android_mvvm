package mhha.sample.android_mvvm.interfaces.command

import java.util.concurrent.atomic.AtomicBoolean

interface ICommand {
    val isEnabled: AtomicBoolean
    fun execute(params: Any?)
    fun notifyCanExecuteChanged()
    var event: IEventListener?
    var asyncEvent: IAsyncEventListener?
    var clickIntervalMillis: Int
}