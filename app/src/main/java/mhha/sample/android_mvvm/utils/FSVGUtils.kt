package mhha.sample.android_mvvm.utils

import android.content.Context
import android.widget.ImageView
import com.pixplicity.sharp.Sharp
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream

class FSVGUtils {
    private var httpClient: OkHttpClient? = null
    fun fetchSVG(context: Context, url: String, target: ImageView, callback: ((call: Call, e: Exception) -> Unit)) {
        if (httpClient == null) {
            httpClient = OkHttpClient.Builder().cache(Cache(context.cacheDir, 100 * 1024 * 1024)).build()
        }
        val request: Request = Request.Builder().url(url).build()
        httpClient!!.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(call, e)
            }
            override fun onResponse(call: Call, response: Response) {
                var stream: InputStream? = null
                try {
                    stream = response.body!!.byteStream()
                    Sharp.loadInputStream(stream).into(target)
                    stream.close()
                } catch (e: Exception) {
                    callback(call, e)
                    stream?.close()
                }
            }
        })
    }
    fun fetchSVG(context: Context, url: String, target: FHexagonMaskView, callback: ((call: Call, e: Exception) -> Unit)) {
        if (httpClient == null) {
            httpClient = OkHttpClient.Builder().cache(Cache(context.cacheDir, 10 * 1024 * 1024)).build()
        }
        val request: Request = Request.Builder().url(url).build()
        httpClient!!.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(call, e)
            }
            override fun onResponse(call: Call, response: Response) {
                var stream: InputStream? = null
                try {
                    stream = response.body!!.byteStream()
                    Sharp.loadInputStream(stream).into(target)
                    stream.close()
                } catch (e: Exception) {
                    callback(call, e)
                    stream?.close()
                }
            }
        })
    }
    fun fetchSVG(context: Context, url: String, target: FCircleStrokeView, callback: ((call: Call, e: Exception) -> Unit)) {
        if (httpClient == null) {
            httpClient = OkHttpClient.Builder().cache(Cache(context.cacheDir, 10 * 1024 * 1024)).build()
        }
        val request: Request = Request.Builder().url(url).build()
        httpClient!!.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(call, e)
            }
            override fun onResponse(call: Call, response: Response) {
                var stream: InputStream? = null
                try {
                    stream = response.body!!.byteStream()
                    Sharp.loadInputStream(stream).into(target)
                    stream.close()
                } catch (e: Exception) {
                    callback(call, e)
                    stream?.close()
                }
            }
        })
    }
}