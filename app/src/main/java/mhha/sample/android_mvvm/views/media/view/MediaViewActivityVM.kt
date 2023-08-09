package mhha.sample.android_mvvm.views.media.view

import android.app.Application
import androidx.multidex.MultiDexApplication
import kotlinx.coroutines.flow.MutableStateFlow
import mhha.sample.android_mvvm.bases.FBaseViewModel
import mhha.sample.android_mvvm.utils.FExtensionUtils

class MediaViewActivityVM(application: MultiDexApplication): FBaseViewModel(application) {
    val mediaViewItem = MutableStateFlow(mutableListOf<MediaViewItemModel>())

    fun setMediaDataList(mediaList: ArrayList<String>?, mediaResTypeList: ArrayList<Int>?, mediaFileTypeList: ArrayList<Int>?) {
        if (mediaList == null) return

        val buff = mutableListOf<MediaViewItemModel>()
        mediaList.forEachIndexed { index, x ->
            val mediaResType = if (!mediaResTypeList.isNullOrEmpty() && mediaResTypeList.size > index) FExtensionUtils.MediaResType.fromIndex(mediaResTypeList[index]) else FExtensionUtils.MediaResType.URL
            val mediaFileType = if (!mediaFileTypeList.isNullOrEmpty() && mediaFileTypeList.size > index) FExtensionUtils.MediaFileType.fromIndex(mediaFileTypeList[index]) else FExtensionUtils.MediaFileType.UNKNOWN
            buff.add(MediaViewItemModel(mediaResType, mediaFileType).setThis(x))
        }
        mediaViewItem.value = buff
    }

    enum class ClickEvent {
        CLOSE,
        OPTION,
    }

    enum class MediaType(val index: Int) {
    }
}