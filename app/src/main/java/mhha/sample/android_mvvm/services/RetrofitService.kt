package mhha.sample.android_mvvm.services

import com.google.gson.GsonBuilder
import mhha.sample.android_mvvm.bases.FConstants
import mhha.sample.android_mvvm.bases.FMainApplication
import mhha.sample.android_mvvm.models.adapter.EmptyStringToNumberTypeAdapter
import mhha.sample.android_mvvm.models.retrofit.AddingTokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {
    private val loggingInterceptor = HttpLoggingInterceptor()
    private fun leveled(): HttpLoggingInterceptor {
        return loggingInterceptor.apply {
            level = if (FMainApplication.ins.isDebug()) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    fun <T> create(service: Class<T>): T = defaultRetrofit().create(service)
    fun <T> create(endPoint: String, service: Class<T>): T = getRetrofit(endPoint).create(service)
    private fun defaultRetrofit() = getRetrofit(getBaseUrl())
    private fun getBaseUrl() = if (FMainApplication.ins.isDebug()) FConstants.REST_API_DEBUG_URL else FConstants.REST_API_URL
    private fun getRetrofit(endPoint: String) = Retrofit.Builder()
        .baseUrl(endPoint)
        .addConverterFactory(GsonConverterFactory.create(getGson()))
        .client(getOkHttpClient())
        .build()
    private fun getOkHttpClient() = OkHttpClient.Builder()
        .addNetworkInterceptor(leveled())
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(AddingTokenInterceptor())
        .build()
    private fun getGson() = GsonBuilder()
        .registerTypeAdapter(Int::class.java, EmptyStringToNumberTypeAdapter())
        .registerTypeAdapter(Int::class.javaPrimitiveType, EmptyStringToNumberTypeAdapter())
        .registerTypeAdapter(Double::class.java, EmptyStringToNumberTypeAdapter())
        .registerTypeAdapter(Double::class.javaPrimitiveType, EmptyStringToNumberTypeAdapter())
        .create()
}