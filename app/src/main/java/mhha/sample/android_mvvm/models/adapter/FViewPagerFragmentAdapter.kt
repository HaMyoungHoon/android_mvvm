package mhha.sample.android_mvvm.models.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

abstract class FViewPagerFragmentAdapter(private val fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    protected abstract val items: MutableList<Fragment>
    protected val holder = mutableListOf<FragmentViewHolder>()
    protected abstract var bundle: Bundle
    protected var recyclerView: RecyclerView? = null
    override fun getItemCount() = items.size
    override fun createFragment(position: Int): Fragment {
        if (position in 0 until  itemCount) {
            val item = items[position]
            item.arguments = bundle
            return item
        }

        throw Exception("out of range")
    }

    override fun onBindViewHolder(holder: FragmentViewHolder, position: Int, payloads: MutableList<Any>) {
        this.holder.add(holder)
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }
    open fun destroyAdapter() {
        items.forEach {
            try {
                val transaction = fragmentManager.beginTransaction()
                if (!it.isAdded) return@forEach
                if (it.isDetached) return@forEach
                if (it.isRemoving) return@forEach
                transaction.remove(it)
                transaction.commit()
            } catch (_: Exception) { }
        }
        if (recyclerView != null) {
            super.onDetachedFromRecyclerView(recyclerView!!)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        items.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}