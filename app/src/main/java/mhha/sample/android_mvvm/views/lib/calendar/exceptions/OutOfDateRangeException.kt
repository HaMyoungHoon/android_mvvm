package mhha.sample.android_mvvm.views.lib.calendar.exceptions

/**
 * Created by Applandeo Team.
 */

data class OutOfDateRangeException(override val message: String) : Exception(message)