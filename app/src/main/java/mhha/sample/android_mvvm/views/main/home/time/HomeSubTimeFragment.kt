package mhha.sample.android_mvvm.views.main.home.time

import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseFragment
import mhha.sample.android_mvvm.databinding.HomeSubTimeFragmentBinding
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.interfaces.command.IEventListener
import mhha.sample.android_mvvm.models.fDate.FDateTime
import mhha.sample.android_mvvm.utils.FCoroutineUtil
import mhha.sample.android_mvvm.utils.FExtensionUtils
import mhha.sample.android_mvvm.views.dialog.time.TimeDialog
import mhha.sample.android_mvvm.views.lib.calendar.CalendarView
import mhha.sample.android_mvvm.views.lib.calendar.DatePicker
import mhha.sample.android_mvvm.views.lib.calendar.builders.DatePickerBuilder
import mhha.sample.android_mvvm.views.lib.calendar.listeners.OnSelectDateListener
import java.util.Calendar

class HomeSubTimeFragment: FBaseFragment<HomeSubTimeFragmentBinding, HomeSubTimeFragmentVM>() {
    override var layoutId = R.layout.home_sub_time_fragment
    override val dataContext: HomeSubTimeFragmentVM by lazy {
        HomeSubTimeFragmentVM(multiDexApplication)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onBindAfter() {
        binding?.dataContext = dataContext
    }
    override fun onViewCreatedAfter() {
        dataContext.addEventListener(object: IAsyncEventListener {
            override suspend fun onEvent(data: Any?) {
                setLayoutCommand(data)
            }
        })
    }
    private fun setLayoutCommand(data: Any?) {
        val eventName = data as? HomeSubTimeFragmentVM.ClickEvent ?: return
        when (eventName) {
            HomeSubTimeFragmentVM.ClickEvent.TIME -> {
                FCoroutineUtil.coroutineScope({
                    timeOpen()
                }, {
                    it.show(childFragmentManager, "")
                })
            }
            HomeSubTimeFragmentVM.ClickEvent.CALENDAR -> {
                FCoroutineUtil.coroutineScope({
                    calendarOpen()
                }, {
                    it?.show()
                })
            }
        }
    }

    private fun timeOpen(): TimeDialog {
        val today = FExtensionUtils.getToday().addMinutes(10.0)
        return TimeDialog(today.toString("HH"), today.toString("mm"), object: IEventListener {
            override fun onEvent(data: Any?) {
                if (data is String) {
                    toast(data)
                }
            }
        })
    }
    private fun calendarOpen(): DatePicker? {
        val context = contextBuff ?: return null
        return DatePickerBuilder(context, object: OnSelectDateListener {
            override fun onSelect(calendar: List<Calendar>) {
                calendar.forEach {
                    val dateTime = FDateTime().setThis(it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DATE))
                    toast(dateTime.toString("yyyy-MM-dd"))
                }
            }
        }).minimumDate(Calendar.getInstance().apply {
            add(Calendar.DATE, -1)
        }).headerColor(R.color.def_background)
            .selectionColor(R.color.teal_200)
            .selectionLabelColor(R.color.primary)
            .pickerType(CalendarView.CLASSIC)
            .build()
    }
}