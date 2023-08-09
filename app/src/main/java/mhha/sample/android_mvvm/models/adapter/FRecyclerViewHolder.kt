package mhha.sample.android_mvvm.models.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class FRecyclerViewHolder<T: ViewDataBinding>: RecyclerView.ViewHolder {
    private var _binding: T?
    val binding get() = _binding
    constructor(view: View): super(view) {
        DataBindingUtil.bind<T>(view).let {
            _binding = it
        }
    }
}