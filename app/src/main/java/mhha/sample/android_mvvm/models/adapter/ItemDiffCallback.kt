package mhha.sample.android_mvvm.models.adapter

import androidx.recyclerview.widget.DiffUtil

class ItemDiffCallback<T: Any>: DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return compare(oldItem, newItem)
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
    private fun compare(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}