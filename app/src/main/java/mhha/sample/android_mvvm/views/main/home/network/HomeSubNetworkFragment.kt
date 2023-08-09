package mhha.sample.android_mvvm.views.main.home.network

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.ContactsContract
import android.provider.Settings
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseFragment
import mhha.sample.android_mvvm.bases.FConstants
import mhha.sample.android_mvvm.databinding.HomeSubNetworkFragmentBinding
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.utils.FNetworkSupport

class HomeSubNetworkFragment: FBaseFragment<HomeSubNetworkFragmentBinding, HomeSubNetworkFragmentVM>() {
    override var layoutId = R.layout.home_sub_network_fragment
    override val dataContext: HomeSubNetworkFragmentVM by lazy {
        HomeSubNetworkFragmentVM(multiDexApplication)
    }
    private var _networkSupport: FNetworkSupport? = null

    override fun onDestroy() {
        contextBuff?.let { x ->
            _networkSupport?.unregisterNetworkCallback(x) {
            }
        }
        _networkSupport = null
        super.onDestroy()
    }
    override fun onBindAfter() {
        binding?.dataContext = dataContext
        initNetworkSupport()
    }
    override fun onViewCreatedAfter() {
        dataContext.addEventListener(object: IAsyncEventListener {
            override suspend fun onEvent(data: Any?) {
                setLayoutCommand(data)
            }
        })
    }
    private fun initNetworkSupport() {
        _networkSupport = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            FNetworkSupport(ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO)
        } else {
            FNetworkSupport()
        }
        _networkSupport?.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
    }
    private fun setLayoutCommand(data: Any?) {
        val eventName = data as? HomeSubNetworkFragmentVM.ClickEvent ?: return
        when (eventName) {
            HomeSubNetworkFragmentVM.ClickEvent.WIFI_SSID -> {
                if (hasPermissionsGranted(FConstants.LOCATION_PERMISSION)) {
                    setNetworkService()
                } else {
                    requestMultiPermissions(FConstants.LOCATION_PERMISSION)
                }
            }
            HomeSubNetworkFragmentVM.ClickEvent.OPEN_CONNECT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    activity?.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activity?.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))
                } else {
                    toast(getResString(R.string.home_sub_network_open_connect_error))
                }
            }
        }
    }

    private fun isWifiEnable(): Boolean {
        val connectivityManager = contextBuff?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
    private fun setNetworkService() {
        if (!isWifiEnable()) {
            toast(getResString(R.string.home_sub_network_wifi_ssid_error))
            return
        }
        dataContext.ssidData.value = ""
        _networkSupport?.addCapabilitiesChanged(object: FNetworkSupport.ICapabilitiesChangedListener {
            override fun callback(network: Network, networkCapabilities: NetworkCapabilities) {
                var ssid = ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val wifiInfo = networkCapabilities.transportInfo as? WifiInfo ?: return
                    ssid = wifiInfo.ssid
                } else {
                    val wifiManager = activity?.getSystemService(Context.WIFI_SERVICE) as? WifiManager ?: return
                    if (wifiManager.isWifiEnabled) {
                        @Suppress("DEPRECATION")
                        ssid = wifiManager.connectionInfo.ssid
                    }
                }

                dataContext.ssidData.value = ssid
                if (ssid.isEmpty() || ssid.contains("<unknown ssid>")) {
                }
            }
        })
        val context = contextBuff ?: return
        _networkSupport?.registerNetworkCallback(context) {
            toast(it)
        }
    }

    override fun onMultiPermission(data: Map<String, Boolean>) {
        if (FConstants.LOCATION_PERMISSION.all { x -> data[x] == true }) {
            setNetworkService()
        } else {
            toast(getResString(R.string.home_sub_network_permission_error))
        }
    }
}