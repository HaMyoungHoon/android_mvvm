package mhha.sample.android_mvvm.views.lib.calendar.listeners

import mhha.sample.android_mvvm.views.lib.calendar.EventDay

/**
 * This interface is used to handle long clicks on calendar cells
 *
 * Created by Applandeo Team.
 */

interface OnDayLongClickListener {
    fun onDayLongClick(eventDay: EventDay)
}