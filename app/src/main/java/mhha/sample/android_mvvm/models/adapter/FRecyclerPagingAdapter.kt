package mhha.sample.android_mvvm.models.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter

abstract class FRecyclerPagingAdapter<T1: Any, T2: ViewDataBinding>(itemDiffCallback: ItemDiffCallback<T1>): PagingDataAdapter<T1, FRecyclerViewHolder<T2>>(itemDiffCallback) {
    protected abstract var layoutId: Int
    protected abstract var lifecycle: Lifecycle
    override fun onBindViewHolder(holder: FRecyclerViewHolder<T2>, position: Int) {
        holder.binding?.let {
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FRecyclerViewHolder<T2> {
        val inflater = LayoutInflater.from(parent.context)
        return FRecyclerViewHolder(inflater.inflate(layoutId, parent, false))
    }
    fun updateItems(data: PagingData<T1>) {
        submitData(lifecycle, data)
    }
    fun findIndex(item: T1): Int = snapshot().indexOfFirst { it == item }
}
