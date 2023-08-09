package mhha.sample.android_mvvm.views.media.picker

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.gun0912.tedpermission.coroutine.TedPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseActivity
import mhha.sample.android_mvvm.databinding.MediaPickerActivityBinding
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.utils.FCoroutineUtil
import mhha.sample.android_mvvm.utils.FExtensionUtils

class MediaPickerActivity: FBaseActivity<MediaPickerActivityBinding, MediaPickerActivityVM>() {
    override var layoutId = R.layout.media_picker_activity
    override val dataContext: MediaPickerActivityVM by lazy {
        MediaPickerActivityVM(multiDexApplication)
    }
    var adapter: MediaPickerActivityAdapter? = null

    override fun onPause() {
        playerStop()
        super.onPause()
    }
    override fun onStart() {
        playerPlay()
        super.onStart()
    }
    override fun onDestroy() {
        playerRelease()
        binding?.exoView?.player = null
        adapter = null
        super.onDestroy()
    }

    override fun afterOnCreate() {
        binding?.dataContext = dataContext
        dataContext.addEventListener(object: IAsyncEventListener {
            override suspend fun onEvent(data: Any?) {
                setLayoutCommand(data)
                setMediaItemCommand(data)
                setVideoCommand(data)
            }
        })
        getPermission()
        adapter = MediaPickerActivityAdapter(this, dataContext.relayCommand, this)
        binding?.rvMediaList?.adapter = adapter
        lifecycleScope.launch {
            dataContext.boxesPosition.collectLatest {
                dataContext.selectItem(it)
            }
        }
//        binding?.spBox?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                dataContext.selectItem(position)
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                dataContext.selectItem(-1)
//            }
//        }
        dataContext.clearItem()
        val mediaPathList = intent.getStringArrayListExtra("mediaPathList")
        val mediaNameList = intent.getStringArrayListExtra("mediaNameList")
        val mediaFileTypeList = intent.getIntegerArrayListExtra("mediaFileTypeList")
        dataContext.ableSelectCountStringSuffix = getString(R.string.media_able_click_suffix_desc)
        dataContext.setPreviousMedia(mediaPathList, mediaNameList, mediaFileTypeList)
        dataContext.setMediaMaxCount(intent.getIntExtra("mediaMaxCount", 0))
        binding?.exoView?.player = ExoPlayer.Builder(this).build()
    }
    private fun getPermission() {
        FCoroutineUtil.coroutineScope({
            TedPermission.create()
                .setRationaleTitle(getString(R.string.albom_permission_title))
                .setRationaleMessage(getString(R.string.read_external_permission_message))
                .setDeniedTitle(getString(R.string.cancel_desc))
                .setDeniedMessage(getString(R.string.denied_permission_title))
                .setGotoSettingButtonText(getString(R.string.permission_setting_desc))
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
        }, {
            if (!it.isGranted) {
                finish()
            } else {
                init()
            }
        })
    }
    private fun init() {
        val mediaList = getImageList()
        getVideoList(mediaList)
        dataContext.setItems(mediaList)
        dataContext.selectItem(0)
    }
    private fun getImageList(): MutableList<Pair<String, MutableList<MediaPickerSourceModel>>> {
        val mediaList = mutableListOf<Pair<String, MutableList<MediaPickerSourceModel>>>()
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = this.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateTimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val folderName = cursor.getString(folderColumn)
                val dateTime = cursor.getString(dateTimeColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val findFolder = mediaList.find { x -> x.first == folderName }
                if (findFolder != null) {
                    findFolder.second.add(MediaPickerSourceModel(uri, name, FExtensionUtils.MediaFileType.IMAGE, dateTime))
                } else {
                    mediaList.add(Pair(folderName, arrayListOf(MediaPickerSourceModel(uri, name, FExtensionUtils.MediaFileType.IMAGE, dateTime))))
                }
            }
        }
        return mediaList
    }
    private fun getVideoList(buff: MutableList<Pair<String, MutableList<MediaPickerSourceModel>>>?): MutableList<Pair<String, MutableList<MediaPickerSourceModel>>> {
        val mediaList = buff ?: mutableListOf()
        val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION)
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val query = this.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            val dateTimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val folderName = cursor.getString(folderColumn)
                val dateTime = cursor.getString(dateTimeColumn)
                val duration = cursor.getLong(durationColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val findFolder = mediaList.find { x -> x.first == folderName }
                if (findFolder != null) {
                    findFolder.second.add(MediaPickerSourceModel(uri, name, FExtensionUtils.MediaFileType.VIDEO, dateTime, duration).generateData())
                } else {
                    mediaList.add(Pair(folderName, arrayListOf(MediaPickerSourceModel(uri, name, FExtensionUtils.MediaFileType.VIDEO, dateTime, duration).generateData())))
                }
            }
        }
        return mediaList
    }

    private fun setLayoutCommand(data: Any?) {
        val eventName = data as? MediaPickerActivityVM.ClickEvent ?: return
        when (eventName) {
            MediaPickerActivityVM.ClickEvent.CLOSE -> {
                setResult(RESULT_CANCELED)
                Glide.get(this).clearMemory()
                finish()
            }
            MediaPickerActivityVM.ClickEvent.CONFIRM -> {
                if (!dataContext.confirmEnable.value) {
                    return
                }
                setResult(RESULT_OK, Intent().apply {
                    putStringArrayListExtra("mediaUris", dataContext.getClickedItemList())
                    putStringArrayListExtra("mediaNames", dataContext.getClickedItemNameList())
                    putIntegerArrayListExtra("mediaFileTypes", dataContext.getClickedItemFileTypeList())
                })
                Glide.get(this).clearMemory()
                finish()
            }
        }
    }
    private fun setMediaItemCommand(data: Any?) {
        if (data !is ArrayList<*> || data.size < 1) return
        val eventName = data[0] as? MediaPickerSourceModel.ClickEvent ?: return
        val dataBuff = data[1] as? MediaPickerSourceModel ?: return
        when (eventName) {
            MediaPickerSourceModel.ClickEvent.SELECT -> {
                val findItem = dataContext.clickItemBuff.firstOrNull { it.mediaPath == dataBuff.mediaPath }
                if (findItem != null) {
                    if (dataContext.mediaPath.value == findItem.mediaPath) {
                        dataContext.removeClickedItem(dataBuff)
                        dataContext.confirmEnable.value = dataContext.getClickItemCount() > 0
                        val lastItem = dataContext.clickItemBuff.lastOrNull()
                        if (lastItem != null) {
                            dataContext.setLastClickedItem(lastItem)
                            dataContext.mediaPath.value = lastItem.mediaPath
                            dataContext.mediaFileType.value = lastItem.mediaFileType
                        }
                    } else {
                        dataContext.setLastClickedItem(dataBuff)
                        dataContext.mediaPath.value = findItem.mediaPath
                        dataContext.mediaFileType.value = findItem.mediaFileType
                    }
                    playerPlay()
                    adapter?.updateItem(dataBuff)
                    dataContext.clickItemBuff.forEach {
                        adapter?.updateItem(it)
                    }
                    return
                }
                if (!dataContext.isPossibleSelect() && !dataBuff.clickState) {
                    return
                }

                dataContext.setLastClickedItem(dataBuff)
                dataContext.appendClickedItem(dataBuff)
                dataContext.mediaPath.value = dataBuff.mediaPath
                dataContext.mediaFileType.value = dataBuff.mediaFileType
                playerPlay()

                adapter?.updateItem(dataBuff)
                dataContext.clickItemBuff.forEach {
                    adapter?.updateItem(it)
                }
                dataContext.confirmEnable.value = dataContext.getClickItemCount() > 0
            }
            MediaPickerSourceModel.ClickEvent.SELECT_LONG -> {

            }
        }
    }
    private var previousView: StyledPlayerView? = null
    private fun setVideoCommand(data: Any?) {
        if (data !is ArrayList<*> || data.size < 1) return
        val view = data[0] as? StyledPlayerView ?: return
        val dataBuff = data[1] as? MediaPickerSourceModel ?: return
        dataBuff.clickState = !dataBuff.clickState
        if (dataBuff.clickState) {
            dataContext.appendClickedItem(dataBuff)
        } else {
            dataContext.removeClickedItem(dataBuff)
        }
        previousView?.player?.stop()
        previousView = view
        previousView?.player?.prepare()
        previousView?.player?.play()
        dataContext.confirmEnable.value = dataContext.getClickItemCount() > 0
    }
    private fun playerPlay() {
        if (dataContext.mediaPath.value == null) {
            return
        }
        if (dataContext.mediaFileType.value != FExtensionUtils.MediaFileType.VIDEO) {
            playerStop()
            return
        }
        dataContext.videoPath.value = dataContext.mediaPath.value
    }
    private fun playerStop() {
        val player = binding?.exoView?.player as? ExoPlayer ?: return
        player.stop()
        dataContext.videoPath.value = null
    }
    private fun playerRelease() {
        val player = binding?.exoView?.player as? ExoPlayer ?: return
        player.stop()
        player.release()
        dataContext.videoPath.value = null
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["circle_number_src", "circle_number_solid", "circle_number_stroke"], requireAll = false)
        fun setCircleNumberTextView(textView: androidx.appcompat.widget.AppCompatTextView, num: Int?, solid: String?, stroke: String?) {
            val drawable = ContextCompat.getDrawable(textView.context, R.drawable.circle)
            val shape = (drawable as? LayerDrawable)?.getDrawable(0) as? GradientDrawable

            if (num == null) {
                textView.text = ""
            } else {
                textView.text = num.toString()
            }
            if (shape == null) {
                return
            }
            if (solid == null) {
                shape.setColor(Color.parseColor("#aa000000"))
            } else {
                shape.setColor(Color.parseColor(solid))
            }
            if (stroke == null) {
                shape.setStroke(1, Color.parseColor("#aaffffff"))
            } else {
                shape.setStroke(1, Color.parseColor(stroke))
            }
            textView.background = shape
        }
        @JvmStatic
        @BindingAdapter("spinner_box_media_picker_items")
        fun setMediaSpinnerItems(spinner: Spinner, spinner_box_media_picker_items: MutableStateFlow<MutableList<String>>?) {
            spinner.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                spinner_box_media_picker_items?.collectLatest {
                    spinner.adapter = ArrayAdapter(spinner.context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, it)
                }
            }
        }
        @JvmStatic
        @BindingAdapter("recycler_media_picker_items")
        fun setMediaRecyclerItems(recyclerView: RecyclerView, recycler_media_picker_items: MutableStateFlow<MutableList<MediaPickerSourceModel>>?) {
            val adapter = recyclerView.adapter as? MediaPickerActivityAdapter ?: return
            adapter.lifecycleOwner.lifecycleScope.launch {
                recycler_media_picker_items?.collectLatest {
                    adapter.updateItems(it)
                }
            }
        }
    }
}