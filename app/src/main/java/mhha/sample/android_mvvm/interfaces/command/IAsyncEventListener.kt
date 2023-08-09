package mhha.sample.android_mvvm.interfaces.command

interface IAsyncEventListener {
    suspend fun onEvent(data: Any?)
}