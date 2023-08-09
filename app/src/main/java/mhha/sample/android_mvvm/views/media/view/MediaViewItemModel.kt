package mhha.sample.android_mvvm.views.media.view

import android.net.Uri
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.utils.FExtensionUtils

data class MediaViewItemModel(
    var mediaResType: FExtensionUtils.MediaResType = FExtensionUtils.MediaResType.URI,
    var mediaFileType: FExtensionUtils.MediaFileType = FExtensionUtils.MediaFileType.UNKNOWN,
    var videoPath: String = "",
    var mediaPath: String = "",
    var videoUriPath: Uri? = null,
    var mediaUriPath: Uri? = null,
) {
    fun setThis(media: String): MediaViewItemModel {
        when (mediaResType) {
            FExtensionUtils.MediaResType.UNKNOWN -> return this
            FExtensionUtils.MediaResType.URI -> {
                when (mediaFileType) {
                    FExtensionUtils.MediaFileType.UNKNOWN -> return this
                    FExtensionUtils.MediaFileType.IMAGE -> try { mediaUriPath = Uri.parse(media) } catch (_: Exception) { }
                    FExtensionUtils.MediaFileType.VIDEO -> try { videoUriPath = Uri.parse(media) } catch (_: Exception) { }
                }
            }
            FExtensionUtils.MediaResType.URL -> {
                when (mediaFileType) {
                    FExtensionUtils.MediaFileType.UNKNOWN -> return this
                    FExtensionUtils.MediaFileType.IMAGE -> mediaPath = media
                    FExtensionUtils.MediaFileType.VIDEO -> videoPath = media
                }
            }
        }

        return this
    }

    var relayCommand: ICommand? = null
    fun onClick(eventName: ClickEvent) {
        relayCommand?.execute(arrayListOf(eventName, this))
    }

    enum class ClickEvent {
        CLICK
    }
}