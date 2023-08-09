package mhha.sample.android_mvvm.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi

class FNetworkSupport: NetworkCallback {
    private var availableListener: IAvailableListener? = null
    private var capabilitiesChangedListener: ICapabilitiesChangedListener? = null
    private var unavailableListener: IUnavailableListener? = null
    private var lostListener: ILostListener? = null
    private var blockedStatusChangedListener: IBlockedStatusChangedListener? = null
    private var losingListener: ILosingListener? = null
    private var linkPropertiesChangedListener: ILinkPropertiesChangedListener? = null
    private val transportTypeList: MutableList<Int> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.S)
    constructor(flag: Int): super(flag) {
    }
    constructor(): super() {
    }
    override fun onAvailable(network: Network) {
        availableListener?.callback(network)
    }
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        capabilitiesChangedListener?.callback(network, networkCapabilities)
    }
    override fun onUnavailable() {
        unavailableListener?.callback()
    }
    override fun onLost(network: Network) {
        lostListener?.callback(network)
    }
    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        blockedStatusChangedListener?.callback(network, blocked)
    }
    override fun onLosing(network: Network, maxMsToLive: Int) {
        losingListener?.callback(network, maxMsToLive)
    }
    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        linkPropertiesChangedListener?.callback(network, linkProperties)
    }

    fun addTransportType(transportType: Int) {
        if (!transportTypeList.contains(transportType)) {
            transportTypeList.add(transportType)
        }
    }
    fun registerNetworkCallback(context: Context, errCallback: (String?) -> Unit) {
        if (transportTypeList.isEmpty()) {
            errCallback("transportType is Empty")
            return
        }
        try {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            var networkRequest = NetworkRequest.Builder()
            transportTypeList.forEach { x ->
                networkRequest = networkRequest.addTransportType(x)
            }
            connectivityManager.registerNetworkCallback(networkRequest.build(), this)
        } catch (e: Exception) {
            errCallback(e.message)
        }
    }
    fun unregisterNetworkCallback(context: Context, errCallback: (String?) -> Unit) {
        transportTypeList.clear()
        try {
            context.getSystemService(ConnectivityManager::class.java).unregisterNetworkCallback(this)
        } catch (e: Exception) {
            errCallback(e.message)
        }
    }
    fun addAvailable(availableListener: IAvailableListener) {
        this.availableListener = availableListener
    }
    fun addCapabilitiesChanged(capabilitiesChangedListener: ICapabilitiesChangedListener) {
        this.capabilitiesChangedListener = capabilitiesChangedListener
    }
    fun addUnavailable(unavailableListener: IUnavailableListener) {
        this.unavailableListener = unavailableListener
    }
    fun addLost(lostListener: ILostListener) {
        this.lostListener = lostListener
    }
    fun addBlockedStatusChanged(blockedStatusChangedListener: IBlockedStatusChangedListener) {
        this.blockedStatusChangedListener = blockedStatusChangedListener
    }
    fun addLosing(losingListener: ILosingListener) {
        this.losingListener = losingListener
    }
    fun addLinkPropertiesChanged(linkPropertiesChangedListener: ILinkPropertiesChangedListener) {
        this.linkPropertiesChangedListener = linkPropertiesChangedListener
    }

    interface IAvailableListener {
        fun callback(network: Network)
    }
    interface ICapabilitiesChangedListener {
        fun callback(network: Network, networkCapabilities: NetworkCapabilities)
    }
    interface IUnavailableListener {
        fun callback()
    }
    interface ILostListener {
        fun callback(network: Network)
    }
    interface IBlockedStatusChangedListener {
        fun callback(network: Network, blocked: Boolean)
    }
    interface ILosingListener {
        fun callback(network: Network, maxMsToLive: Int)
    }
    interface ILinkPropertiesChangedListener {
        fun callback(network: Network, linkProperties: LinkProperties)
    }
}