package mhha.sample.android_mvvm.utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import mhha.sample.android_mvvm.R

class FLocationUtil(val context: Context, val once: Boolean, val listener: ILocationListener): LocationListener, LocationCallback() {
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationManager: LocationManager? = null
    var googlePlay = false
    private var minTime = 500L
    private var minDistance = 1F
    companion object {
        fun getDistance(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Int {
            return Location("point A").apply {
                latitude = latitude1
                longitude = longitude1
            }.distanceTo(Location("point B").apply {
                latitude = latitude2
                longitude = longitude2
            }).toInt()
        }
    }
    init {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initThis()
        } else {
            listener.onLocationFail(context.getString(R.string.permit_need_message))
        }
    }
    private fun initThis() {
        if (isGooglePlayServicesAvailable()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            googlePlay = true
        } else {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }
    fun setUpdateConfig(minTime: Long, minDistance: Float) {
        this.minTime = if (minTime > 100L) minTime else 100L
        this.minDistance = if (minDistance > 0.5F) minDistance else 0.5F
    }
    fun stopWatching() {
        if (googlePlay) {
            fusedLocationClient?.removeLocationUpdates(this)
        } else {
            locationManager?.removeUpdates(this)
        }
    }
    fun getLocation() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googlePlay) {
                getFused()
            } else {
                getDefault()
            }
        }
    }
    private fun getFused() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, minTime).build()
            fusedLocationClient?.requestLocationUpdates(locationRequest, this, Looper.getMainLooper())
        }
    }
    private fun getDefault() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this)
        }
    }

    override fun onLocationChanged(p0: Location) {
        if (once) {
            stopWatching()
        }
        listener.onLocationEvent(p0.latitude, p0.longitude)
    }
    override fun onLocationResult(p0: LocationResult) {
        if (once) {
            stopWatching()
        }
        val location = p0.lastLocation ?: return
        listener.onLocationEvent(location.latitude, location.longitude)
    }
    fun isGooglePlayServicesAvailable(): Boolean {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }



    interface ILocationListener {
        fun onLocationEvent(latitude: Double, longitude: Double)
        fun onLocationFail(excetion: String)
    }
}