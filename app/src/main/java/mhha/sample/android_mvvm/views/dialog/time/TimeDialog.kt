package mhha.sample.android_mvvm.views.dialog.time

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseDialogFragment
import mhha.sample.android_mvvm.databinding.TimeDialogBinding
import mhha.sample.android_mvvm.interfaces.IOnDismissListener
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.interfaces.command.IEventListener
import java.util.ArrayList

class TimeDialog(
    private var hour: String,
    private var minute: String,
    private var eventListener: IEventListener,
    private val setOnDismissListener: IOnDismissListener = object: IOnDismissListener {
        override fun onDismiss() {
        }
    }
): FBaseDialogFragment<TimeDialogBinding, TimeDialogVM>() {
    override var layoutId = R.layout.time_dialog
    override val dataContext: TimeDialogVM by lazy {
        TimeDialogVM(multiDexApplication)
    }

    override fun onBindAfter() {
        binding?.dataContext = dataContext
        dataContext.addEventListener(object: IAsyncEventListener {
            override suspend fun onEvent(data: Any?) {
                setLayoutCommand(data)
            }
        })
        if (hour != "" && minute != "") {
            dataContext.hour.value = (hour.toInt())
            dataContext.minute.value = (minute.toInt() / 5)
        }

        setTimePickerInterval()
    }
    override fun onResume() {
        super.onResume()
        dialogFragmentResize()
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setOnDismissListener.onDismiss()
    }
    private fun setLayoutCommand(data: Any?) {
        val eventName = data as? TimeDialogVM.ClickEvent ?: return
        when (eventName) {
            TimeDialogVM.ClickEvent.CLOSE -> dismiss()
            TimeDialogVM.ClickEvent.CANCEL -> {
                eventListener.onEvent("NO")
                dismiss()
            }
            TimeDialogVM.ClickEvent.SAVE -> {
                eventListener.onEvent("${dataContext.hour.value}:${(dataContext.minute.value) * 5}")
                dismiss()
            }
        }
    }
    private fun setTimePickerInterval() {
        try {
            val minutePicker = binding?.tp?.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android")) as? NumberPicker ?: return
            minutePicker.minValue = 0
            minutePicker.maxValue = 60 / 5 - 1
            val displayedValues: MutableList<String> = ArrayList()
            var i = 0
            while (i < 60) {
                displayedValues.add(String.format("%02d", i))
                i += 5
            }
            minutePicker.displayedValues = displayedValues.toTypedArray()
        } catch (_: Exception) { }
    }
    private fun dialogFragmentResize() {
        val width = 0.9f
        val height = 0.52f
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val window = this.dialog?.window
            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val window = this.dialog?.window
            val x = (rect.width() * width).toInt()
            val params: ViewGroup.LayoutParams? = window?.attributes
            params?.width = x
            window?.attributes = params as WindowManager.LayoutParams
        }
    }
}