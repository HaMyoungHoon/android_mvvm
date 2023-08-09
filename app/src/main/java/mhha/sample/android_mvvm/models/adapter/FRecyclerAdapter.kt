package mhha.sample.android_mvvm.models.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow

abstract class FRecyclerAdapter<T1: ViewDataBinding, T2>: RecyclerView.Adapter<FRecyclerViewHolder<T1>>() {
    protected var items = MutableStateFlow(mutableListOf<T2>())
    protected abstract var layoutId: Int
    protected abstract var lifecycle: Lifecycle
    override fun getItemCount() = items.value.size
    override fun onBindViewHolder(holder: FRecyclerViewHolder<T1>, position: Int) {
        holder.binding?.let {
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FRecyclerViewHolder<T1> {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder = FRecyclerViewHolder<T1>(inflater.inflate(layoutId, parent, false))
        viewHolder.binding?.lifecycleOwner = parent.findViewTreeLifecycleOwner()
        return viewHolder
    }

    fun clearItems() {
        val callback = ListItemDiffCallback(items.value, arrayListOf())
        val result = DiffUtil.calculateDiff(callback)
        items.value = arrayListOf()
        result.dispatchUpdatesTo(this)
    }
    fun updateItems(data: MutableList<T2>?) {
        if (data == null) {
            return
        }
        val callback = ListItemDiffCallback(items.value, data)
        val result = DiffUtil.calculateDiff(callback)
        items.value = data
        result.dispatchUpdatesTo(this)
    }
    fun addItems(data: ArrayList<T2>) {
        val itemsBuff = items.value.toMutableList()
        itemsBuff.addAll(data)
        val callback = ListItemDiffCallback(items.value, itemsBuff)
        val result = DiffUtil.calculateDiff(callback)
        items.value = itemsBuff
        result.dispatchUpdatesTo(this)
    }
}