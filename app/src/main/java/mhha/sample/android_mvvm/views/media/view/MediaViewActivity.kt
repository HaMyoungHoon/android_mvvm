package mhha.sample.android_mvvm.views.media.view

import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseActivity
import mhha.sample.android_mvvm.databinding.MediaViewActivityBinding
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener

class MediaViewActivity: FBaseActivity<MediaViewActivityBinding, MediaViewActivityVM>() {
    override var layoutId = R.layout.media_view_activity
    override val dataContext: MediaViewActivityVM by lazy {
        MediaViewActivityVM(multiDexApplication)
    }

    override fun afterOnCreate() {
        binding?.dataContext = dataContext
        dataContext.addEventListener(object: IAsyncEventListener {
            override suspend fun onEvent(data: Any?) {
                setLayoutCommand(data)
                setMediaCommand(data)
            }
        })
        val mediaList = intent.getStringArrayListExtra("mediaList")
        val mediaResTypeList = intent.getIntegerArrayListExtra("mediaResTypeList")
        val mediaFileTypeList = intent.getIntegerArrayListExtra("mediaFileTypeList")
        dataContext.setMediaDataList(mediaList, mediaResTypeList, mediaFileTypeList)
        binding?.vpMedia?.adapter = MediaViewActivityAdapter(this, dataContext.mediaViewItem.value, dataContext.relayCommand)
    }
    private fun setLayoutCommand(data: Any?) {
        val eventName = data as? MediaViewActivityVM.ClickEvent ?: return
        when (eventName) {
            MediaViewActivityVM.ClickEvent.CLOSE -> finish()
            MediaViewActivityVM.ClickEvent.OPTION -> { }
        }
    }
    private fun setMediaCommand(data: Any?) {
        if (data !is ArrayList<*> || data.size < 1) return
        val eventName = data[0] as? MediaViewItemModel.ClickEvent ?: return
        val dataBuff = data[1] as? MediaViewItemModel ?: return
        when (eventName) {
            MediaViewItemModel.ClickEvent.CLICK -> { }
        }
    }
}