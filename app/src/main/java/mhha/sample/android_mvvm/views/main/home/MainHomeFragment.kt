package mhha.sample.android_mvvm.views.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseFragment
import mhha.sample.android_mvvm.databinding.MainHomeFragmentBinding
import mhha.sample.android_mvvm.databinding.MainHomeTabItemBinding

class MainHomeFragment: FBaseFragment<MainHomeFragmentBinding, MainHomeFragmentVM>() {
    override var layoutId = R.layout.main_home_fragment
    override val dataContext: MainHomeFragmentVM by lazy {
        MainHomeFragmentVM(multiDexApplication)
    }
    private var _viewAdapter: MainHomeFragmentAdapter? = null
    override fun onDestroy() {
        _viewAdapter?.destroyAdapter()
        _viewAdapter = null
        super.onDestroy()
    }

    override fun onBindAfter() {
        binding?.dataContext = dataContext
    }

    override fun onViewCreatedAfter() {
        setViewPager()
        setTabLayout()
    }
    private fun setViewPager() {
        val binding = binding ?: return
        _viewAdapter = MainHomeFragmentAdapter(childFragmentManager, viewLifecycleOwner)
        binding.vpMiddle.isUserInputEnabled = true
        binding.vpMiddle.adapter = _viewAdapter
        binding.vpMiddle.apply {
            setCurrentItem(0, false)
        }
    }
    private fun setTabLayout() {
        val binding = binding ?: return
        TabLayoutMediator(binding.tlMiddle, binding.vpMiddle) { tab, position ->
            when (position) {
                0 -> getTabView(tab, getResString(R.string.main_home_menu_1_desc))
                1 -> getTabView(tab, getResString(R.string.main_home_menu_2_desc))
            }
        }.attach()
        binding.tlMiddle.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectTab(tab)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                unSelectTab(tab)
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.tlMiddle.apply {
            this@MainHomeFragment.selectTab(getTabAt(0))
        }
    }

    private fun getTabView(tab: TabLayout.Tab?, text: String) {
        if (tab == null) return
        val context = contextBuff ?: return
        val binding = binding ?: return
        val inflater = LayoutInflater.from(context)
        val view: MainHomeTabItemBinding = DataBindingUtil.inflate(inflater, R.layout.main_home_tab_item, binding.tlMiddle, false)
        view.tvText.text = text
        view.tvText.visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        tab.customView = view.root
    }
    private fun selectTab(tab: TabLayout.Tab?) {
        if (tab == null) return
        val customView = tab.customView as? ViewGroup ?: return
        if (customView.childCount > 0 && customView.getChildAt(0) is AppCompatTextView) {
            val textView = customView.getChildAt(0) as? AppCompatTextView ?: return
            textView.setTextColor(getResColor(R.color.absolute_white))
            textView.setBackgroundResource(R.drawable.shape_home_tab_item_primary_gradient_background)
        }
    }
    private fun unSelectTab(tab: TabLayout.Tab?) {
        if (tab == null) return
        val customView = tab.customView as? ViewGroup ?: return
        if (customView.childCount > 0 && customView.getChildAt(0) is AppCompatTextView) {
            val textView = customView.getChildAt(0) as? AppCompatTextView ?: return
            textView.setTextColor(getResColor(R.color.black))
            textView.setBackgroundResource(R.drawable.shape_home_tab_item_background)
        }
    }
}