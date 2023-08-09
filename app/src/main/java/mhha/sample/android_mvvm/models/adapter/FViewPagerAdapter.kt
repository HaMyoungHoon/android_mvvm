package mhha.sample.android_mvvm.models.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class FViewPagerAdapter<T: ViewDataBinding>: RecyclerView.Adapter<FRecyclerViewHolder<T>>() {
    protected abstract var layoutId: Int
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FRecyclerViewHolder<T> {
        val bind = LayoutInflater.from(parent.context)
        return FRecyclerViewHolder(bind.inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: FRecyclerViewHolder<T>, position: Int) {
        holder.binding?.let {
        }
    }
}