package mhha.sample.android_mvvm.views.lib.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.databinding.DatePickerBinding
import mhha.sample.android_mvvm.interfaces.IOnDismissListener
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.interfaces.command.IEventListener
import mhha.sample.android_mvvm.models.command.RelayCommand
import mhha.sample.android_mvvm.views.lib.calendar.CalendarView.Companion.CLASSIC
import mhha.sample.android_mvvm.views.lib.calendar.listeners.*
import mhha.sample.android_mvvm.views.lib.calendar.utils.*

/**
 * This class is responsible for creating DatePicker dialog.
 *
 * Created by Applandeo Team.
 */

class DatePicker(
    private val context: Context,
    private val calendarProperties: CalendarProperties,
    private val setOnDismissListener: IOnDismissListener = object: IOnDismissListener {
        override fun onDismiss() {
        }
    }
) {
    val relayCommand: ICommand = RelayCommand({})
    lateinit var binding: DatePickerBinding
    fun show(): DatePicker {
        binding = DatePickerBinding.inflate(LayoutInflater.from(context))
        val alertDialog = MaterialAlertDialogBuilder(context, com.google.android.material.R.style.MaterialAlertDialog_Material3).create().apply {
            setView(binding.root)
        }

        if (calendarProperties.pagesColor != 0) {
            binding.root.setBackgroundColor(calendarProperties.pagesColor)
        }

        setTodayButtonVisibility(binding.todayButton)
        setDialogButtonsColors(binding.negativeButton, binding.todayButton)
        setOkButtonState(calendarProperties.calendarType == CalendarView.ONE_DAY_PICKER, binding.positiveButton)
        setDialogButtonsTypeface(binding.root)

        val calendarView = CalendarView(context = context, properties = calendarProperties)
        (relayCommand as? RelayCommand)?.addEventListener(object: IEventListener {
            override fun onEvent(data: Any?) {
                if (data !is String) return
                when (data) {
                    "negativeButton" -> alertDialog.cancel()
                    "positiveButton" -> {
                        alertDialog.cancel()
                        calendarProperties.onSelectDateListener?.onSelect(calendarView.selectedDates)
                    }
                    "todayButton" -> {
                        calendarView.showCurrentMonthPage()
                    }
                }
            }
        })

        calendarProperties.onSelectionAbilityListener = { enabled ->
            setOkButtonState(enabled, binding.positiveButton)
        }
        calendarProperties.calendar?.let {
            runCatching { calendarView.setDate(it) }
        }

        binding.calendarContainer.addView(calendarView)
        binding.vwLine.isVisible = calendarProperties.calendarType != CLASSIC
        binding.llButton.isVisible = calendarProperties.calendarType != CLASSIC
        binding.negativeButton.setOnClickListener {
            relayCommand.execute("negativeButton")
        }

        binding.positiveButton.setOnClickListener {
            relayCommand.execute("positiveButton")
        }

        binding.todayButton.setOnClickListener {
            relayCommand.execute("todayButton")
        }

        calendarView.setOnCloseClickListener(object : OnCloseClickListener {
            override fun onCloseClick() {
                alertDialog.cancel()
            }
        })

        alertDialog.setOnDismissListener {
            this.setOnDismissListener.onDismiss()
        }
        alertDialog.show()

        return this
    }

    private fun setDialogButtonsTypeface(view: View) {
        calendarProperties.typeface?.let { typeface ->
            binding.todayButton.typeface = typeface
            binding.negativeButton.typeface = typeface
            binding.positiveButton.typeface = typeface
        }
    }

    private fun setDialogButtonsColors(
        negativeButton: androidx.appcompat.widget.AppCompatTextView,
        todayButton: androidx.appcompat.widget.AppCompatButton
    ) {
        if (calendarProperties.dialogButtonsColor != 0) {
            negativeButton.setTextColor(
                ContextCompat.getColor(
                    context,
                    calendarProperties.dialogButtonsColor
                )
            )
            todayButton.setTextColor(
                ContextCompat.getColor(
                    context,
                    calendarProperties.dialogButtonsColor
                )
            )
        }
    }

    private fun setOkButtonState(enabled: Boolean, okButton: androidx.appcompat.widget.AppCompatTextView) {
        okButton.isEnabled = enabled

        if (calendarProperties.dialogButtonsColor == 0) return

        val stateResource = if (enabled) {
            calendarProperties.dialogButtonsColor
        } else {
            R.color.color_1F000000
        }

        okButton.setTextColor(ContextCompat.getColor(context, stateResource))
    }

    private fun setTodayButtonVisibility(todayButton: AppCompatButton) {
        calendarProperties.maximumDate?.let {
            if (it.isMonthBefore(midnightCalendar) || it.isMonthAfter(midnightCalendar)) {
                todayButton.visibility = View.GONE
            }
        }
    }
}
