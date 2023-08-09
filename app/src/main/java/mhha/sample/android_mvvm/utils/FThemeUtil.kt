package mhha.sample.android_mvvm.utils

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

class FThemeUtil {
    companion object {
        const val LIGHT_MODE = "light"
        const val DARK_MODE = "dark"
        const val DEFAULT_MODE = "default"

        fun applyTheme(mode: ThemeMode? = null) {
            when (mode) {
                ThemeMode.LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                ThemeMode.DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    }
                }
            }
        }

        enum class ThemeMode(val str: String) {
            LIGHT_MODE("light"),
            DARK_MODE("dark"),
            DEFAULT_MODE("default")
        }
    }
}