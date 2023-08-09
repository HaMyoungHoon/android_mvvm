package mhha.sample.android_mvvm.views.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import mhha.sample.android_mvvm.models.adapter.FViewPagerFragmentAdapter
import mhha.sample.android_mvvm.views.main.home.network.HomeSubNetworkFragment
import mhha.sample.android_mvvm.views.main.home.time.HomeSubTimeFragment

class MainHomeFragmentAdapter(fragmentManager: FragmentManager, lifecycleOwner: LifecycleOwner): FViewPagerFragmentAdapter(fragmentManager, lifecycleOwner.lifecycle) {
    override val items = mutableListOf<Fragment>()
    override var bundle = Bundle().apply {
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        items.add(HomeSubTimeFragment())
        items.add(HomeSubNetworkFragment())
        super.onAttachedToRecyclerView(recyclerView)
    }

}