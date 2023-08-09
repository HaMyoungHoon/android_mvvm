package mhha.sample.android_mvvm

import androidx.lifecycle.LifecycleOwner
import mhha.sample.android_mvvm.databinding.ListItemMainMenuBinding
import mhha.sample.android_mvvm.interfaces.command.ICommand
import mhha.sample.android_mvvm.models.adapter.FRecyclerAdapter
import mhha.sample.android_mvvm.models.adapter.FRecyclerViewHolder

class MainActivityAdapter(val lifecycleOwner: LifecycleOwner, val relayCommand: ICommand): FRecyclerAdapter<ListItemMainMenuBinding, MainMenuListModel>() {
    override var layoutId = R.layout.list_item_main_menu
    override var lifecycle = lifecycleOwner.lifecycle
    override fun onBindViewHolder(holder: FRecyclerViewHolder<ListItemMainMenuBinding>, position: Int) {
        holder.binding?.let {
            val item = items.value[position]
            item.relayCommand = relayCommand
            it.lifecycleOwner = lifecycleOwner
            it.dataContext = item
        }
    }
}