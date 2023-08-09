package mhha.sample.android_mvvm.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.edit

object FUtils {
    private const val HOME_MENU_INDEX = "homeMenuIndex"

    fun getHomeMenuIndex(context: Context) = getInt(context, HOME_MENU_INDEX)
    fun setHomeMenuIndex(context: Context, data: Int) = putInt(context, HOME_MENU_INDEX, data)

    private fun getBool(context: Context, keyName: String, defData: Boolean = false) = FExtensionUtils.cryptoSharedPreferences(context).getBoolean(keyName, defData)
    private fun putBool(context: Context, keyName: String, data: Boolean) = FExtensionUtils.cryptoSharedPreferences(context).apply { edit { putBoolean(keyName, data) } }
    private fun getInt(context: Context, keyName: String, defData: Int = -1) = FExtensionUtils.cryptoSharedPreferences(context).getInt(keyName, defData)
    private fun putInt(context: Context, keyName: String, data: Int) = FExtensionUtils.cryptoSharedPreferences(context).apply { edit { putInt(keyName, data) } }
    private fun getFloat(context: Context, keyName: String, defData: Float = -1F) = FExtensionUtils.cryptoSharedPreferences(context).getFloat(keyName, defData)
    private fun putFloat(context: Context, keyName: String, data: Float) = FExtensionUtils.cryptoSharedPreferences(context).apply { edit { putFloat(keyName, data) } }
    private fun getString(context: Context, keyName: String, defData: String = "") = FExtensionUtils.cryptoSharedPreferences(context).getString(keyName, defData)
    private fun putString(context: Context, keyName: String, data: String) = FExtensionUtils.cryptoSharedPreferences(context).apply { edit { putString(keyName, data) } }
    private fun removeData(context: Context, keyName: String) = FExtensionUtils.cryptoSharedPreferences(context).apply { edit { remove(keyName) } }
    inline fun <reified T: Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key)
    }
    inline fun <reified T: Parcelable> Bundle.parcelable(intent: Intent, key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> intent.getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") intent.getParcelableExtra(key)
    }
}