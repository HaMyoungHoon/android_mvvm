package mhha.sample.android_mvvm.utils

import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.GeocodingResult
import java.util.EnumSet
import java.util.Locale

class FGooglePlaySupport {
    private var _mapOptions: MapOptions = MapOptions.of(MapOption.NONE)
    private var _googleMap: GoogleMap? = null
    private var _googleMarker: Marker? = null
    private var _googleCircle: Circle? = null
    private var _latitude: Double = 0.0
    private var _longitude: Double = 0.0
    private var _address: String = ""
    private var _zoom: Float = 15F
    private var _title: String = ""
    private var _snippet: String = ""
    private var _apiKey: String = ""
    private var _customLanguage: String = ""
    private var _firstZoomOn = false
    val latitude get() = _latitude
    val longitude get() = _longitude
    val address get() =_address

    // region config
    fun addConfig(mapOption: MapOption) {
        _mapOptions.add(mapOption)
    }
    fun setConfig(mapOptions: MapOptions) {
        _mapOptions.clear()
        _mapOptions.addAll(mapOptions)
    }
    fun setZoom(zoom: Float) {
        if (zoom > 0F) {
            _zoom = zoom
        }
        _googleMap?.moveCamera(CameraUpdateFactory.zoomTo(_zoom))
    }
    fun deleteConfig(mapOption: MapOption) {
        _mapOptions.remove(mapOption)
    }
    // endregion

    // region set map
    fun setGoogleMap(p0: GoogleMap) {
        _googleMap = p0
    }
    fun setMarkClickListener(callback: (Marker) -> Unit) {
        if (_googleMap == null) return
        _googleMap?.setOnMarkerClickListener {
            callback(it)
            true
        }
    }
    fun setInfoClickListener(callback: (Marker) -> Unit) {
        if (_googleMap == null) return
        _googleMap?.setOnInfoWindowClickListener {
            callback(it)
        }
    }
    fun setMapClickListener(callback: (LatLng) -> Unit) {
        if (_googleMap == null) return
        _googleMap?.setOnMapClickListener {
            callback(it)
        }
    }
    // endregion

    // set marker
    fun setMarkerOption(lat: Double, lng: Double, icon: Bitmap? = null): String? {
        return setMarkerOption(LatLng(lat, lng), icon)
    }
    fun setMarkerOption(lat: Double, lng: Double, icon: Int? = null): String? {
        return setMarkerOption(LatLng(lat, lng), icon)
    }
    fun setMarkerOption(latLng: LatLng, icon: Bitmap? = null): String? {
        if (_googleMap == null) return "map is null"
        _googleMarker?.remove()
        try {
            val markerOption = MarkerOptions().position(latLng)
            icon.let {
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(it!!))
            }
            _googleMarker = _googleMap?.addMarker(markerOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun setMarkerOption(latLng: LatLng, icon: Int? = null): String? {
        if (_googleMap == null) return "map is null"
        _googleMarker?.remove()
        try {
            val markerOption = MarkerOptions().position(latLng)
            icon.let {
                markerOption.icon(BitmapDescriptorFactory.fromResource(it!!))
            }
            _googleMarker = _googleMap?.addMarker(markerOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun setMarkerOption(icon: Bitmap? = null): String? {
        if (_googleMap == null) return "map is null"
        _googleMarker?.remove()
        try {
            val markerOption = MarkerOptions().position(LatLng(_latitude, _longitude))
            icon.let {
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(it!!))
            }
            _googleMarker = _googleMap?.addMarker(markerOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun setMarkerOption(icon: Int? = null): String? {
        if (_googleMap == null) return "map is null"
        _googleMarker?.remove()
        try {
            val markerOption = MarkerOptions().position(LatLng(_latitude, _longitude))
            icon.let {
                markerOption.icon(BitmapDescriptorFactory.fromResource(it!!))
            }
            _googleMarker = _googleMap?.addMarker(markerOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    // endregion

    // region set circle
    fun setCircleOption(lat: Double, lng: Double, radius: Double, strokeColor: Int, fillColor: Int): String? {
        return setCircleOption(LatLng(lat, lng), radius, strokeColor, fillColor)
    }
    fun setCircleOption(latLng: LatLng, radius: Double, strokeColor: Int, fillColor: Int): String? {
        if (_googleMap == null) return "map is null"
        _googleCircle?.remove()
        try {
            val circleOption = CircleOptions().center(latLng).radius(radius).strokeColor(strokeColor).fillColor(fillColor)
            _googleCircle = _googleMap?.addCircle(circleOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun setCircleOption(radius: Double, strokeColor: Int, fillColor: Int): String? {
        if (_googleMap == null) return "map is null"
        _googleCircle?.remove()
        try {
            val circleOption = CircleOptions().center(LatLng(_latitude, _longitude)).radius(radius).strokeColor(strokeColor).fillColor(fillColor)
            _googleCircle = _googleMap?.addCircle(circleOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    // endregion

    // region common command
    fun setLocation(lat: Double, lng: Double, zoom: Float? = null): String {
        return setLocation(LatLng(lat, lng), zoom)
    }
    fun setLocation(latLng: LatLng, zoom: Float? = null): String {
        if (_googleMap == null) return "map is null"
        if (zoom != null && zoom > 0F) _zoom = zoom
        _latitude = latLng.latitude
        _longitude = latLng.longitude
        if (_mapOptions.contains(MapOption.SET_LOCATION_AND_MOVE)) {
            return moveLocation(_latitude, _longitude, _zoom)
        }

        return ""
    }
    fun moveLocation(lat: Double, lng: Double, zoom: Float? = null): String {
        return moveLocation(LatLng(lat, lng), zoom)
    }
    fun moveLocation(latLng: LatLng, zoom: Float? = null): String {
        if (_googleMap == null) return "map is null"
        if (zoom != null && zoom > 0F) _zoom = zoom
        _latitude = latLng.latitude
        _longitude = latLng.longitude
        return moveLocation()
    }
    fun moveLocation(): String {
        val latLng = LatLng(_latitude, _longitude)
        return try {
            if (_googleMarker == null) {
                _googleMarker = _googleMap?.addMarker(MarkerOptions().position(latLng))
            } else {
                _googleMarker?.position = latLng
            }
            if (_googleCircle == null) {
                _googleCircle = _googleMap?.addCircle(CircleOptions().center(latLng))
            } else {
                _googleCircle?.center = latLng
            }
            if (_mapOptions.contains(MapOption.MOVE_AND_INFO_SHOW)) {
                showInfo()
            }
            if (_mapOptions.contains(MapOption.MOVE_AND_CENTER)) {
                return setCenter()
            } else {
                ""
            }
        } catch (e: Exception) {
            e.message ?: "exception"
        }
    }
    fun setCenter(): String {
        if (_googleMap == null) return "map is null"
        return try {
            if (_mapOptions.contains(MapOption.CENTER_AND_ZOOM)) {
                _googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(_latitude, _longitude), _zoom))
            } else {
                _googleMap?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(_latitude, _longitude)))
            }
            ""
        } catch (e: Exception) {
            e.message ?: "exception"
        }
    }
    fun setRadius(radius: Int): String {
        return setRadius(radius.toDouble())
    }
    fun setRadius(radius: Double): String {
        if (_googleMap == null) return "map is null"
        if (_googleCircle == null) {
            _googleCircle = _googleMap?.addCircle(CircleOptions().radius(radius))
        } else {
            _googleCircle?.radius = radius
        }
        return ""
    }
    fun setInfo(title: String, snippet: String) {
        _title = title
        _snippet = snippet
        _googleMarker?.title = _title
        _googleMarker?.snippet = _snippet
    }
    fun showInfo() {
        if (_googleMarker != null) {
            _googleMarker?.showInfoWindow()
        }
    }
    fun hideInfo() {
        if (_googleMarker != null) {
            _googleMarker?.hideInfoWindow()
        }
    }
    // endregion

    // region search
    fun setAPIKey(apiKey: String) {
        _apiKey = apiKey
    }
    fun setCustomLanguage(lang: String) {
        _customLanguage = lang
    }
    fun getLanguage(): String {
        return if (_customLanguage.isEmpty()) {
            Locale.getDefault().language
        } else {
            _customLanguage
        }
    }
    fun searchGeocoding(lat: Double, lng: Double, lang: String = getLanguage(), callback: (Array<GeocodingResult>) -> Unit, error: (String?) -> Unit) {
        searchGeocoding(LatLng(lat, lng), lang, callback, error)
    }
    fun searchGeocoding(latLng: LatLng, lang: String = getLanguage(), callback: (Array<GeocodingResult>) -> Unit, error: (String?) -> Unit) {
        if (_apiKey.isEmpty()) {
            error("api key is empty")
            return
        }
        FCoroutineUtil.coroutineScope({
            try {
                val geoContext = GeoApiContext.Builder().apiKey(_apiKey).build()
                GeocodingApi.newRequest(geoContext).language(lang).latlng(com.google.maps.model.LatLng(latLng.latitude, latLng.longitude)).await()
            } catch (e: Exception) {
                error(e.message)
                arrayOf()
            }
        }, {
            callback(it)
        })
    }
    fun searchGeocoding(lang: String = getLanguage(), callback: (Array<GeocodingResult>) -> Unit, error: (String?) -> Unit) {
        if (_apiKey.isEmpty()) {
            error("api key is empty")
            return
        }
        FCoroutineUtil.coroutineScope({
            try {
                val geoContext = GeoApiContext.Builder().apiKey(_apiKey).build()
                GeocodingApi.newRequest(geoContext).language(lang).latlng(com.google.maps.model.LatLng(_latitude, _longitude)).await()
            } catch (e: Exception) {
                error(e.message)
                arrayOf()
            }
        }, {
            callback(it)
        })
    }
    fun searchGeocoding(placeName: String, lang: String = getLanguage(), callback: (Array<GeocodingResult>) -> Unit, error: (String?) -> Unit) {
        if (_apiKey.isEmpty()) {
            error("api key is empty")
            return
        }
        FCoroutineUtil.coroutineScope({
            try {
                val geoContext = GeoApiContext.Builder().apiKey(_apiKey).build()
                GeocodingApi.newRequest(geoContext).language(lang).address(placeName).await()
            } catch (e: Exception) {
                error(e.message)
                arrayOf()
            }
        }, {
            callback(it)
        })
    }
    fun searchGeocoder(context: Context, lat: Double, lng: Double, locale: Locale = Locale.getDefault(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
        val geocoder = Geocoder(context, locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                geocoder.getFromLocation(lat, lng, 5, object: Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        callback(addresses)
                    }
                    override fun onError(errorMessage: String?) {
                        error(errorMessage)
                    }
                })
            } catch (e: Exception) {
                error(e.message)
            }
        } else {
            try {
                @Suppress("DEPRECATION")
                val ret = geocoder.getFromLocation(lat, lng, 5)
                callback(ret!!)
            } catch (e: Exception) {
                error(e.message)
            }
        }
    }
    fun searchGeocoder(context: Context, latLng: LatLng, locale: Locale = Locale.getDefault(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
        searchGeocoder(context, latLng.latitude, latLng.longitude, locale, callback, error)
    }
    fun searchGeocoder(context: Context, locale: Locale = Locale.getDefault(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
        val geocoder = Geocoder(context, locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                geocoder.getFromLocation(_latitude, _longitude, 5, object: Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        callback(addresses)
                    }
                    override fun onError(errorMessage: String?) {
                        error(errorMessage)
                    }
                })
            } catch (e: Exception) {
                error(e.message)
            }
        } else {
            try {
                @Suppress("DEPRECATION")
                val ret = geocoder.getFromLocation(_latitude, _longitude, 5)
                callback(ret!!)
            } catch (e: Exception) {
                error(e.message)
            }
        }
    }
    fun searchGeocoder(context: Context, placeName: String, locale: Locale = Locale.getDefault(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
        val geocoder = Geocoder(context, locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                geocoder.getFromLocationName(placeName, 5, object: Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        callback(addresses)
                    }
                    override fun onError(errorMessage: String?) {
                        error(errorMessage)
                    }
                })
            } catch (e: Exception) {
                error(e.message)
            }
        } else {
            try {
                @Suppress("DEPRECATION")
                val ret = geocoder.getFromLocationName(placeName, 5)
                callback(ret!!)
            } catch (e: Exception) {
                error(e.message)
            }
        }
    }
    // endregion

    enum class MapOption {
        NONE,
        SET_LOCATION_AND_MOVE,
        MOVE_AND_CENTER,
        CENTER_AND_ZOOM,
        MOVE_AND_INFO_SHOW;
        infix fun and(rhs: MapOption): EnumSet<MapOption> = MapOptions.of(this, rhs)
    }
    infix fun MapOptions.allOf(rhs: MapOptions) = this.containsAll(rhs)
    infix fun MapOptions.and(rhs: MapOption): EnumSet<MapOption> = MapOptions.of(rhs, *this.toTypedArray())
}
typealias MapOptions = EnumSet<FGooglePlaySupport.MapOption>