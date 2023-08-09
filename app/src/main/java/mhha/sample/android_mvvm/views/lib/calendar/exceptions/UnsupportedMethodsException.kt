package mhha.sample.android_mvvm.views.lib.calendar.exceptions

/**
 * Created by Applandeo Team.
 */

data class UnsupportedMethodsException(override val message: String) : RuntimeException(message)