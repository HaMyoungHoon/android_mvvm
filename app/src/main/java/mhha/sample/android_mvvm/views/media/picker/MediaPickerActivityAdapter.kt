package mhha.sample.android_mvvm.views.media.picker

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.databinding.ListItemMediaPickerBinding
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.models.adapter.FRecyclerAdapter
import mhha.sample.android_mvvm.models.adapter.FRecyclerViewHolder

class MediaPickerActivityAdapter(val lifecycleOwner: LifecycleOwner, val relayCommand: ICommand, val motherContext: Context): FRecyclerAdapter<ListItemMediaPickerBinding, MediaPickerSourceModel>() {
    override var layoutId = R.layout.list_item_media_picker
    override var lifecycle = lifecycleOwner.lifecycle
    override fun onBindViewHolder(holder: FRecyclerViewHolder<ListItemMediaPickerBinding>, position: Int) {
        holder.binding?.let {
            val item = items.value[position]
            item.relayCommand = relayCommand
            it.lifecycleOwner = lifecycleOwner
            it.dataContext = item
//            playerSetting(it, item)
        }
    }

    fun updateItem(data: MediaPickerSourceModel?) {
        val index = items.value.indexOfFirst { it == data }
        if (index < 0) {
            return
        }
        notifyItemChanged(index)
    }
}