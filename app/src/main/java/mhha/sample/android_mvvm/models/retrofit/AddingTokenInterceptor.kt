package mhha.sample.android_mvvm.models.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class AddingTokenInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newBuilder = chain.request().newBuilder()
        val accessKey = FRetrofitVariable.tokenModel.accessKey
        val secretKey = FRetrofitVariable.tokenModel.getTimedSecretKey()
        val timeStamp = FRetrofitVariable.tokenModel.getTimeStamp()

        return if (!accessKey.isNullOrEmpty() && !secretKey.isNullOrEmpty() && timeStamp.isNotEmpty()) {
            chain.proceed(newBuilder
                .addHeader("access-key", accessKey)
                .addHeader("secret-key", secretKey)
                .addHeader("time-stamp", timeStamp)
                .build())
        } else if (!accessKey.isNullOrEmpty()) {
            chain.proceed(newBuilder
                .addHeader("access-key", accessKey)
                .build())
        } else {
            chain.proceed(newBuilder.build())
        }
    }
}