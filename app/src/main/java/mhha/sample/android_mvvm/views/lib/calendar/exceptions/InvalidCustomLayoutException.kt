package mhha.sample.android_mvvm.views.lib.calendar.exceptions

object InvalidCustomLayoutException : RuntimeException("YOUR CUSTOM CALENDAR DAY LAYOUT MUST CONTAIN A TextView WITH android:id=\"@+id/dayLabel\"")