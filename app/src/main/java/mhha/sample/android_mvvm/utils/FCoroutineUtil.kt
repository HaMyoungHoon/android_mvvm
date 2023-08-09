package mhha.sample.android_mvvm.utils

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FCoroutineUtil {
    fun <T> cancellableCoroutineScope(scope: suspend CoroutineScope.() -> T, method: (() -> Unit)? = null, timeoutMillis: Long = 3000) {
        CoroutineScope(Dispatchers.Main).launch {
            withTimeoutOrNull(timeoutMillis) {
                val context = if (this.coroutineContext.isActive) Dispatchers.Main else Dispatchers.IO
                withContext(context) {
                    suspendCancellableCoroutine<Unit> { continuation ->
                        launch {
                            try {
                                scope()
                                method?.invoke()
                                continuation.resume(Unit)
                            } catch (e: Exception) {
                                continuation.resumeWithException(e)
                            }
                        }
                    }
                }
            }
        }
    }
    fun <T> coroutineScope(scope: suspend CoroutineScope.() -> T, result: ((T) -> Unit)? = null, method: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            val context = if (this.coroutineContext.isActive) Dispatchers.Main else Dispatchers.IO
            withContext(context) {
                scope().also {
                    result?.invoke(it)
                    method?.invoke()
                }
            }
        }
    }
    fun <T> coroutineScope(lifecycleCoroutineScope: LifecycleCoroutineScope, scope: suspend CoroutineScope.() -> T, result: ((T) -> Unit), method: (() -> Unit)? = null) {
        lifecycleCoroutineScope.launch {
            val context = if (this.coroutineContext.isActive) Dispatchers.Main else Dispatchers.IO
            withContext(context) {
                scope()
                method?.invoke()
            }
        }
    }
}