package mhha.sample.android_mvvm.views.lib.calendar.utils

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.views.lib.calendar.*
import mhha.sample.android_mvvm.views.lib.calendar.CalendarView.Companion.CLASSIC
import mhha.sample.android_mvvm.views.lib.calendar.exceptions.ErrorsMessages
import mhha.sample.android_mvvm.views.lib.calendar.exceptions.UnsupportedMethodsException
import mhha.sample.android_mvvm.views.lib.calendar.listeners.*
import java.util.*

/**
 * This class contains all properties of the calendar
 *
 * Created by Applandeo Team.
 */

typealias OnPagePrepareListener = (Calendar) -> List<CalendarDay>

class CalendarProperties(private val context: Context) {

    var calendarType: Int = CLASSIC

    var headerColor: Int = 0
        get() = if (field <= 0) field else context.parseColor(field)

    var headerLabelColor: Int = 0
        get() = if (field <= 0) field else context.parseColor(field)

    var selectionColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.secondary) else field

    var todayLabelColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.primary) else field

    var sunDayLabelColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.red) else field

    var saturDayLabelColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.primary) else field

    var sunDayDisabledLabelColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.red2) else field

    var saturDayDisabledLabelColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.purple_200) else field

    var todayColor: Int = 0
        get() = if (field <= 0) field else context.parseColor(field)

    var dialogButtonsColor: Int = 0

    var itemLayoutResource: Int = R.layout.calendar_view_day

    var selectionBackground: Int = R.drawable.circle

    var disabledDaysLabelsColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.gray) else field

    var highlightedDaysLabelsColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.gray) else field

    var pagesColor: Int = 0

    var abbreviationsBarColor: Int = 0

    var abbreviationsLabelsColor: Int = 0

    var abbreviationsLabelsSize: Float = 0F

    var daysLabelsColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.def_foreground) else field

    var selectionLabelColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.def_background) else field

    var anotherMonthsDaysLabelsColor: Int = 0
        get() = if (field == 0) context.parseColor(R.color.gray) else field

    var headerVisibility: Int = View.VISIBLE

    var typeface: Typeface? = null

    var todayTypeface: Typeface? = null

    var abbreviationsBarVisibility: Int = View.VISIBLE

    var navigationVisibility: Int = View.VISIBLE

    var eventsEnabled: Boolean = false

    var swipeEnabled: Boolean = true

    var selectionDisabled: Boolean = false

    var previousButtonSrc: Drawable? = null

    var forwardButtonSrc: Drawable? = null

    var selectionBetweenMonthsEnabled: Boolean = false

    val firstPageCalendarDate: Calendar = midnightCalendar

    var firstDayOfWeek = firstPageCalendarDate.firstDayOfWeek

    var calendar: Calendar? = null

    var minimumDate: Calendar? = null

    var maximumDate: Calendar? = null

    var maximumDaysRange: Int = 0

    var onDayClickListener: OnDayClickListener? = null

    var onDayLongClickListener: OnDayLongClickListener? = null

    var onSelectDateListener: OnSelectDateListener? = null

    var onSelectionAbilityListener: OnSelectionAbilityListener? = null

    var onPreviousPageChangeListener: OnCalendarPageChangeListener? = null

    var onForwardPageChangeListener: OnCalendarPageChangeListener? = null

    var onCloseClickListener: OnCloseClickListener? = null

    var eventDays: List<EventDay> = mutableListOf()

    var calendarDayProperties: MutableList<CalendarDay> = mutableListOf()

    var disabledDays: List<Calendar> = mutableListOf()
        set(disabledDays) {
            selectedDays = selectedDays.filter {
                !disabledDays.contains(it.calendar)
            }.toMutableList()

            field = disabledDays.map { it.setMidnight() }.toList()
        }

    var highlightedDays: List<Calendar> = mutableListOf()
        set(highlightedDays) {
            field = highlightedDays.map { it.setMidnight() }.toList()
        }

    var selectedDays = mutableListOf<SelectedDay>()
        private set

    fun setSelectedDay(calendar: Calendar) = setSelectedDay(SelectedDay(calendar))

    fun setSelectedDay(selectedDay: SelectedDay) {
        selectedDays.clear()
        selectedDays.add(selectedDay)
    }

    @Throws(UnsupportedMethodsException::class)
    fun setSelectDays(days: List<Calendar>) {
//        if (calendarType == CalendarView.ONE_DAY_PICKER) {
//            throw UnsupportedMethodsException(ErrorsMessages.ONE_DAY_PICKER_MULTIPLE_SELECTION)
//        }

        if (calendarType == CalendarView.RANGE_PICKER && !days.isFullDatesRange()) {
            throw UnsupportedMethodsException(ErrorsMessages.RANGE_PICKER_NOT_RANGE)
        }

        selectedDays = days
            .map { SelectedDay(it.setMidnight()) }
            .filterNot { it.calendar in disabledDays }
            .toMutableList()
    }

    var onPagePrepareListener: OnPagePrepareListener? = null

    fun findDayProperties(calendar: Calendar): CalendarDay? =
        calendarDayProperties.find { it.calendar.isEqual(calendar) }

    companion object {
        /**
         * A number of months (pages) in the calendar
         * 2401 months means 1200 months (100 years) before and 1200 months after the current month
         */
        const val CALENDAR_SIZE = 2401
        const val FIRST_VISIBLE_PAGE = CALENDAR_SIZE / 2
    }
}
