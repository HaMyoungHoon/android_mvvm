package mhha.sample.android_mvvm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mhha.sample.android_mvvm.bases.FBaseActivity
import mhha.sample.android_mvvm.bases.FConstants
import mhha.sample.android_mvvm.bases.FMainApplication
import mhha.sample.android_mvvm.bases.FNotificationService
import mhha.sample.android_mvvm.databinding.MainActivityBinding
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.utils.FUtils
import mhha.sample.android_mvvm.views.google.map.GoogleMapActivity
import mhha.sample.android_mvvm.views.media.picker.MediaPickerActivity

class MainActivity: FBaseActivity<MainActivityBinding, MainActivityVM>() {
    override var layoutId = R.layout.main_activity
    override val dataContext: MainActivityVM by lazy {
        MainActivityVM(multiDexApplication)
    }
    private var _backPressed: OnBackPressedCallback? = null
    private var _menuAdapter: MainActivityAdapter? = null
    private var _viewAdapter: MainActivityViewAdapter? = null
    private var _alarmCheckResult: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _alarmCheckResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return@registerForActivityResult
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    toast("set Alarm2")
                    dataContext.setAlarm(this@MainActivity, "test2", 1)
                } else {
                    toast("알람을 허용하지 않으면 울릴 수 없습니다.")
                }
            }
        }
    }
    override fun onDestroy() {
        _backPressed = null
        _alarmCheckResult?.unregister()
        _alarmCheckResult = null
        _menuAdapter = null
        _viewAdapter = null
        super.onDestroy()
    }
    override fun afterOnCreate() {
        binding?.dataContext = dataContext
        dataContext.addEventListener(object: IAsyncEventListener {
            override suspend fun onEvent(data: Any?) {
                setLayoutCommand(data)
                setMenuItemCommand(data)
            }
        })
        setBackPressed()
        setMenuItem()
        setViewPager()
        parseAlarmType()
    }
    private fun setBackPressed() {
        _backPressed = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // do something
            }
        }
        this.onBackPressedDispatcher.addCallback(this, _backPressed!!)
    }
    private fun setLayoutCommand(data: Any?) {
        val eventName = data as? MainActivityVM.ClickEvent ?: return
        when (eventName) {
            else -> {}
        }
    }
    private fun setMenuItemCommand(data: Any?) {
        if (data !is ArrayList<*> || data.size < 1) return
        val eventName = data[0] as? MainMenuListModel.ClickEvent ?: return
        val dataBuff = data[1] as? MainMenuListModel ?: return
        when (eventName) {
            MainMenuListModel.ClickEvent.SELECT -> {
                when (dataBuff.index) {
                    0 -> binding?.vpFragment?.setCurrentItem(0, false)
//                    1 -> binding?.vpFragment?.setCurrentItem(1, false)
//                    2 -> binding?.vpFragment?.setCurrentItem(2, false)
                    3 -> {
                        if (hasPermissionsGranted(FConstants.LOCATION_PERMISSION)) {
                            startActivity(Intent(this, GoogleMapActivity::class.java))
                        } else {
                            requestLocationPermissions()
                        }
                    }
                    4 -> startActivity(Intent(this, MediaPickerActivity::class.java).apply {
                        putExtra("mediaMaxCount", 5)
                    })
                    5 -> {
                        val builder = CustomTabsIntent.Builder()
                        builder.build().launchUrl(this, Uri.parse("https://www.naver.com"))
                    }
                }
            }
        }
    }
    private fun setMenuItem() {
        _menuAdapter = MainActivityAdapter(this, dataContext.relayCommand)
        binding?.rvMenu?.adapter = _menuAdapter
        val ret = mutableListOf<MainMenuListModel>()
        ret.add(MainMenuListModel(0, R.drawable.buff_img_1, "home"))
        ret.add(MainMenuListModel(1, R.drawable.buff_img_2, "menu2"))
        ret.add(MainMenuListModel(2, R.drawable.buff_img_3, "menu3"))
        ret.add(MainMenuListModel(3, android.R.drawable.ic_dialog_map, "google map"))
        ret.add(MainMenuListModel(4, android.R.drawable.ic_menu_gallery, "picker"))
        ret.add(MainMenuListModel(5, android.R.drawable.ic_menu_view, "naver"))
        dataContext.menuItems.value = ret
    }
    private fun setViewPager() {
        _viewAdapter = MainActivityViewAdapter(supportFragmentManager, this)
        binding?.vpFragment?.isUserInputEnabled = false
        binding?.vpFragment?.adapter = _viewAdapter
        binding?.vpFragment?.apply {
            val homeMenuIndex = FUtils.getHomeMenuIndex(context).takeIf { it >= 0 } ?: 0
            setCurrentItem(homeMenuIndex, false)
        }
    }
    private fun calcHash() {
        val hashString = FMainApplication.ins.iWantHashString()
        val tag = FMainApplication.ins.getBuildTags()
        val publicHashString = FMainApplication.ins.iWantPublicKeyHashString()
//        dataContext.somethingText.value = "hashString : $hashString\ntag : $tag\npublicHashString: $publicHashString"
    }

    private fun shouldShowRequestPermissionRationale(permissions: Array<String>) = permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }
    private fun hasPermissionsGranted(permissions: Array<String>) = permissions.none {
        ContextCompat.checkSelfPermission((this), it) != PackageManager.PERMISSION_GRANTED
    }
    private fun requestLocationPermissions() {
        if (!shouldShowRequestPermissionRationale(FConstants.LOCATION_PERMISSION)) {
            requestPermissions(FConstants.LOCATION_PERMISSION, FConstants.Permit.LOCATION.index)
        }
    }
    private fun requestAlarmPermissions() {
        if (!shouldShowRequestPermissionRationale(FConstants.ALARM_PERMISSION)) {
            requestPermissions(FConstants.ALARM_PERMISSION, FConstants.Permit.ALARM.index)
        }
    }
    private fun alarmCheck(callback: () -> Unit) {
        if (!hasPermissionsGranted(FConstants.ALARM_PERMISSION)) {
            requestAlarmPermissions()
            return
        }
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                callback()
                return
            }

            val exactAlarmIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            try {
                _alarmCheckResult?.launch(exactAlarmIntent)
            } catch (e: Exception) {
                toast(e.message)
                return
            }
        }
    }
    private fun parseAlarmType() {
        val extras = intent.extras
        val buff = extras?.getInt("alarmType", 0)
        when (FNotificationService.NotifyType.fromIndex(buff)) {
            FNotificationService.NotifyType.DEFAULT -> {
                dataContext.offVibrate()
                dataContext.offSound()
            }
            FNotificationService.NotifyType.WITHOUT_VIBRATE -> {
                dataContext.offSound()
            }
            FNotificationService.NotifyType.WITHOUT_SOUND -> {
                dataContext.offVibrate()
            }
            FNotificationService.NotifyType.WITHOUT_VIBRATE_AND_SOUND -> {
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FConstants.Permit.LOCATION.index -> startActivity(Intent(this, GoogleMapActivity::class.java))
            else -> {

            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("recycler_main_menu_item")
        fun setMainMenuItemRecycler(recyclerView: RecyclerView, recycler_main_menu_item: MutableStateFlow<MutableList<MainMenuListModel>>?) {
            val adapter = recyclerView.adapter as? MainActivityAdapter ?: return
            adapter.lifecycleOwner.lifecycleScope.launch {
                recycler_main_menu_item?.collectLatest {
                    adapter.updateItems(it)
                }
            }
        }
    }
}