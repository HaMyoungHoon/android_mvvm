package mhha.sample.android_mvvm.views.media.view

import androidx.lifecycle.LifecycleOwner
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.databinding.ListItemMediaViewPagerBinding
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.models.adapter.FRecyclerViewHolder
import mhha.sample.android_mvvm.models.adapter.FViewPagerAdapter

class MediaViewActivityAdapter(val lifecycleOwner: LifecycleOwner, val dataModel: MutableList<MediaViewItemModel>, val relayCommand: ICommand): FViewPagerAdapter<ListItemMediaViewPagerBinding>() {
    override var layoutId = R.layout.list_item_media_view_pager
    override fun getItemCount() = dataModel.size
    override fun onBindViewHolder(holder: FRecyclerViewHolder<ListItemMediaViewPagerBinding>, position: Int) {
        holder.binding?.let {
            val item = dataModel[position]
            item.relayCommand = relayCommand
            it.lifecycleOwner = lifecycleOwner
            it.dataContext = item
        }
    }
}