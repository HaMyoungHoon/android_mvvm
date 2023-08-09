package mhha.sample.android_mvvm.views.google.map

import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.widget.SeekBar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.maps.model.AddressComponentType
import com.google.maps.model.AddressType
import com.google.maps.model.GeocodingResult
import mhha.sample.android_mvvm.BuildConfig
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.bases.FBaseActivity
import mhha.sample.android_mvvm.bases.FConstants
import mhha.sample.android_mvvm.databinding.GoogleMapActivityBinding
import mhha.sample.android_mvvm.interfaces.command.IAsyncEventListener
import mhha.sample.android_mvvm.utils.FGooglePlaySupport
import mhha.sample.android_mvvm.utils.FImageUtils
import mhha.sample.android_mvvm.utils.FLocationUtil
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import java.util.concurrent.locks.ReentrantLock

class GoogleMapActivity: FBaseActivity<GoogleMapActivityBinding, GoogleMapActivityVM>(), OnMapReadyCallback, FLocationUtil.ILocationListener {
    override var layoutId = R.layout.google_map_activity
    override val dataContext: GoogleMapActivityVM by lazy {
        GoogleMapActivityVM(multiDexApplication)
    }
    var googleMap = FGooglePlaySupport()
    var locationUtil: FLocationUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.googleMap?.onCreate(savedInstanceState)
    }

    override fun afterOnCreate() {
        val binding = super.binding ?: return
        googleMap.setAPIKey(BuildConfig.GOOGLE_API_KEY)
        binding.dataContext = dataContext
        dataContext.addEventListener(object: IAsyncEventListener {
            override suspend fun onEvent(data: Any?) {
                setLayoutCommand(data)
            }
        })
        binding.googleMap.getMapAsync(this)
        binding.sbRange.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 1) {
                    seekBar?.progress = 1
                    dataContext.setRange(1)
                } else {
                    dataContext.setRange(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                googleMap.setRadius(dataContext.circleRadius.value)
            }
        })
        locationUtil = FLocationUtil(this, true, this)
    }
    override fun onResume() {
        super.onResume()
        binding?.googleMap?.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding?.googleMap?.onPause()
    }
    override fun onDestroy() {
        threadTerminate = true
        super.onDestroy()
        binding?.googleMap?.onDestroy()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding?.googleMap?.onSaveInstanceState(outState)
    }
    override fun onMapReady(p0: GoogleMap) {
        queueThread.start()
        googleMap.setGoogleMap(p0)
        googleMap.setAPIKey(BuildConfig.GOOGLE_API_KEY)
        googleMap.setConfig(FGooglePlaySupport.MapOption.SET_LOCATION_AND_MOVE and FGooglePlaySupport.MapOption.MOVE_AND_CENTER)
        googleMap.setZoom(18F)
        googleMap.setMarkerOption(FImageUtils.imageLoad(this, R.drawable.marker_pin_primary))
        val radius = intent.getIntExtra("radius", 100).toDouble()
        googleMap.setCircleOption(radius, R.color.teal_700, R.color.teal_200)
        googleMap.setMapClickListener { x ->
            googleMap.setLocation(x)
//            googleMap.searchGeocoding({ y ->
//                if (y.isNotEmpty()) {
//                    val streetName = y.find { z -> z.types.find { a1 -> a1 == AddressType.STREET_ADDRESS } != null }
//                    dataContext.address.value = streetName?.formattedAddress ?: ""
//                    val addressComponent = streetName?.addressComponents?.find { z -> z.types.find { a1 -> a1 == AddressComponentType.COUNTRY } != null }
//                    googleMap.setInfo(dataContext.address.value, "shortName:${addressComponent?.shortName}")
//                    googleMap.showInfo()
//                }
//            }, { y -> toast(y) })
//            googleMap.searchGeocoder(this, { y ->
//                if (y.isNotEmpty()) {
//                    val streetName = y.firstOrNull { z -> !z.postalCode.isNullOrEmpty() && z.maxAddressLineIndex >= 0 }
//                    dataContext.address.value = streetName?.getAddressLine(0) ?: ""
//                    val countryCode = y.firstOrNull { z -> !z.countryCode.isNullOrEmpty() }?.countryCode ?: ""
//                    googleMap.setInfo(dataContext.address.value, "countryCode:$countryCode")
//                    googleMap.showInfo()
//                }
//            }, { y -> toast(y) })
            queueClear()
            googleMap.searchGeocoder(this, Locale.KOREAN, { y -> if (y.isNotEmpty()) queue.add(y) }, { y ->
                toast(y)
                queueClear()
            })
            googleMap.searchGeocoder(this, Locale.ENGLISH, { y -> if (y.isNotEmpty()) queue.add(y) }, { y ->
                toast(y)
                queueClear()
            })
            googleMap.searchGeocoder(this, Locale.JAPANESE, { y -> if (y.isNotEmpty()) queue.add(y) }, { y ->
                toast(y)
                queueClear()
            })
        }
        val latitude = intent.getDoubleExtra("latitude", 91.0)
        val longitude = intent.getDoubleExtra("longitude", 181.0)
        if (latitude >= 91.0 || longitude >= 181.0) {
            locationUtil?.getLocation()
        } else {
            googleMap.setLocation(latitude, longitude)
        }
    }
    val queue: Queue<MutableList<Address>> = LinkedList()
    val lock = ReentrantLock()
    var threadTerminate = false
    var queueThread = Thread {
        while (!threadTerminate) {
            if (getQueueSize() >= 3) {
                setInfo()
            }
            Thread.sleep(100)
        }
    }
    private fun getQueueSize(): Int {
        var queueSize = 0
        lock.lock()
        queueSize = queue.size
        lock.unlock()
        return queueSize
    }
    private fun queueClear() {
        lock.lock()
        queue.clear()
        lock.unlock()
    }
    private fun setInfo() {
        var address = ""
        var countryCode = ""
        lock.lock()
        while (queue.size > 0) {
            val buff = queue.remove()
            val streetName = buff.firstOrNull { x -> !x.postalCode.isNullOrEmpty() && x.maxAddressLineIndex >= 0 }
            address += streetName?.getAddressLine(0) ?: ""
            address += "\n"
            countryCode += buff.firstOrNull { x -> !x.countryCode.isNullOrEmpty() }?.countryCode ?: ""
            countryCode += "\n"
        }
        lock.unlock()
        runOnUiThread {
            if (address.replace("\n", "").isNotEmpty()) {
                dataContext.testText.value = "address :\n$address\n countryCode :\n$countryCode"
            } else {
                dataContext.testText.value = ""
            }
        }
    }

    override fun onLocationEvent(latitude: Double, longitude: Double) {
        googleMap.setLocation(latitude, longitude)
    }
    override fun onLocationFail(excetion: String) {
        toast(excetion)
    }
    private fun setLayoutCommand(data: Any?) {
        val eventName = data as GoogleMapActivityVM.ClickEvent ?: return
        when (eventName) {
            GoogleMapActivityVM.ClickEvent.CLOSE -> {
                threadTerminate = true
                finish()
            }
            GoogleMapActivityVM.ClickEvent.SAVE -> resultOK()
        }
    }
    private fun resultOK() {
        threadTerminate = true
        setResult(RESULT_OK, Intent().apply {
            putExtra("latitude", googleMap.latitude)
            putExtra("longitude", googleMap.longitude)
            putExtra("address", dataContext.address.value)
            putExtra("radius", dataContext.circleRadius.value)
        })
        finish()
    }
}