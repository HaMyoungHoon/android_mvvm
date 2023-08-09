package mhha.sample.android_mvvm.views.media.picker

import android.net.Uri
import android.view.View
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.utils.FExtensionUtils

data class MediaPickerSourceModel(
    var mediaPath: Uri? = null,
    var mediaName: String = "",
    var mediaFileType: FExtensionUtils.MediaFileType = FExtensionUtils.MediaFileType.UNKNOWN,
    var mediaDateTime: String = "",
    var duration: Long = -1L,
    var clickState: Boolean = false,
    var num: Int? = null,
    var solid: String? = null,
    var stroke: String? = null,
    var lastClick: Boolean = false,
    var durationString: String = "",
) {
    fun generateData(): MediaPickerSourceModel {
        durationString = FExtensionUtils.getDurationToTime(duration)
        return this
    }
    var relayCommand: ICommand? = null
    fun onClick(eventName: ClickEvent) {
        relayCommand?.execute(arrayListOf(eventName, this))
    }
    fun onVideoClick(view: View) {
        relayCommand?.execute(arrayListOf(view, this))
    }
    fun onLongClick(eventName: ClickEvent): Boolean {
        relayCommand?.execute(arrayListOf(eventName, this))
        return true
    }

    enum class ClickEvent {
        SELECT,
        SELECT_LONG,
    }
}