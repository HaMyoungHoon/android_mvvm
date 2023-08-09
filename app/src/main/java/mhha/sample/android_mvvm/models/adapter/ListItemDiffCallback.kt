package mhha.sample.android_mvvm.models.adapter

import androidx.recyclerview.widget.DiffUtil

class ListItemDiffCallback<T>(oldItems: MutableList<T>, newItems: MutableList<T>): DiffUtil.Callback() {
    private val _oldItems = oldItems
    private val _newItems = newItems
    override fun getOldListSize() = _oldItems.size
    override fun getNewListSize() = _newItems.size
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return _oldItems[oldItemPosition].hashCode() == _newItems[newItemPosition].hashCode()
    }
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return _oldItems[oldItemPosition] == _newItems[newItemPosition]
    }
}