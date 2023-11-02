package mhha.sample.android_mvvm.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
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
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.model.GeocodingResult
import mhha.sample.android_mvvm.R
import mhha.sample.android_mvvm.databinding.CustomClusterViewBinding
import java.util.EnumSet
import java.util.Locale

// map style https://mapstyle.withgoogle.com/

class FGooglePlaySupport {
    private var _mapOptions: MapOptions = MapOptions.of(MapOption.NONE)
    private var _googleMap: GoogleMap? = null
    private var _clusterManager: ClusterManager<MarkerClusterDataModel>? = null
    private var _googleMarker: Marker? = null
    private var _subMarker: MutableList<Marker> = arrayListOf()
    private var _googleCircle: Circle? = null
    private var _latitude: Double = 0.0
    private var _longitude: Double = 0.0
    private var _address: String = ""
    private var _zoom: Float = 15F
    private var _title: String = ""
    private var _snippet: String = ""
    private var _apiKey: String = ""
    private var _customLocale: Locale? = null
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
    fun setMyLocationEnable(context: Context, enable: Boolean) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        _googleMap?.isMyLocationEnabled = enable
    }
    fun setZoom(zoom: Float) {
        if (zoom > 0F) {
            _zoom = zoom
        }
        _googleMap?.moveCamera(CameraUpdateFactory.zoomTo(_zoom))
    }
    fun addZoom(zoom: Float) {
        _zoom += zoom
        _googleMap?.moveCamera(CameraUpdateFactory.zoomTo(_zoom))
    }
    fun deleteConfig(mapOption: MapOption) {
        _mapOptions.remove(mapOption)
    }
    // endregion

    // region set map
    fun setGoogleMap(p0: GoogleMap) {
        _googleMap = p0
        _googleMap?.uiSettings?.let { x ->
            x.isMapToolbarEnabled = false
            x.isMyLocationButtonEnabled = false
        }
    }
    fun setClusterManager(context: Context, markerClusterClickListener: IMarkerClusterClickListener) {
        val buff = _googleMap ?: return
        _clusterManager = ClusterManager(context, buff)
        _clusterManager?.let { x ->
            x.renderer = FClusterRenderer(context, buff, x, markerClusterClickListener)
            buff.setOnCameraIdleListener(x)
        }
    }
    fun setClusterMarkerClickListener(callback: (Cluster<MarkerClusterDataModel>) -> Unit) {
        _clusterManager?.renderer?.setOnClusterClickListener {
            callback(it)
            true
        }
    }
    fun setMarkClickListener(callback: (Marker) -> Unit) {
        _googleMap?.setOnMarkerClickListener {
            callback(it)
            true
        }
    }
    fun setInfoClickListener(callback: (Marker) -> Unit) {
        _googleMap?.setOnInfoWindowClickListener {
            callback(it)
        }
    }
    fun setMapClickListener(callback: (LatLng) -> Unit) {
        _googleMap?.setOnMapClickListener {
            callback(it)
        }
    }
    fun setCameraIdleListener(listener: GoogleMap.OnCameraIdleListener) {
        _googleMap?.setOnCameraIdleListener(listener)
    }
    fun setCameraMoveListener(listener: GoogleMap.OnCameraMoveListener) {
        _googleMap?.setOnCameraMoveListener(listener)
    }
    fun setCameraMoveCanceledListener(listener: GoogleMap.OnCameraMoveCanceledListener) {
        _googleMap?.setOnCameraMoveCanceledListener(listener)
    }
    fun setCameraMoveStartedListener(listener: GoogleMap.OnCameraMoveStartedListener) {
        _googleMap?.setOnCameraMoveStartedListener(listener)
    }
    // endregion

    // set marker
    fun setMarkerOption(lat: Double, lng: Double, icon: Bitmap? = null): String? {
        return setMarkerOption(LatLng(lat, lng), icon)
    }
    fun setMarkerOption(lat: Double, lng: Double, icon: Int? = null): String? {
        return setMarkerOption(LatLng(lat, lng), icon)
    }
    fun setMarkerOption(lat: Double, lng: Double, icon: String): String? {
        return setMarkerOption(LatLng(lat, lng), icon)
    }
    fun setMarkerOption(latLng: LatLng, icon: Bitmap? = null): String? {
        if (_googleMap == null) return "map is null"
        _googleMarker?.remove()
        try {
            val markerOption = MarkerOptions().position(latLng)
            icon?.let {
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(it))
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
            icon?.let {
                markerOption.icon(BitmapDescriptorFactory.fromResource(it))
            }
            _googleMarker = _googleMap?.addMarker(markerOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun setMarkerOption(latLng: LatLng, iconUrl: String): String? {
        if (_googleMap == null) return "map is null"
        _googleMarker?.remove()
        try {
            val markerOption = MarkerOptions().position(latLng)
            val icon = FImageUtils.urlToBitmap(iconUrl)
            markerOption.icon(icon)
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
            icon?.let {
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(it))
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
            icon?.let {
                markerOption.icon(BitmapDescriptorFactory.fromResource(it))
            }
            _googleMarker = _googleMap?.addMarker(markerOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun setMarkerOption(iconUrl: String): String? {
        if (_googleMap == null) return "map is null"
        _googleMarker?.remove()
        try {
            val markerOption = MarkerOptions().position(LatLng(_latitude, _longitude))
            val icon = FImageUtils.urlToBitmap(iconUrl)
            markerOption.icon(icon)
            _googleMarker = _googleMap?.addMarker(markerOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun addMarkerOption(lat: Double, lng: Double, icon: Bitmap? = null): String? {
        return addMarkerOption(LatLng(lat, lng), icon)
    }
    fun addMarkerOption(lat: Double, lng: Double, icon: Int? = null): String? {
        return addMarkerOption(LatLng(lat, lng), icon)
    }
    fun addMarkerOption(lat: Double, lng: Double, icon: String): String? {
        return addMarkerOption(LatLng(lat, lng), icon)
    }
    fun addMarkerCustomView(context: Context, lat: Double, lng: Double, name: String, image: String, catchResource: Int): String? {
        return addMarkerCustomView(context, LatLng(lat, lng), name, image, catchResource)
    }
    fun addMarkerOption(latLng: LatLng, icon: Bitmap? = null): String? {
        val findBuff = _subMarker.find { x -> x.position == latLng }
        if (findBuff != null) {
            return "already add"
        }
        try {
            val markerOption = MarkerOptions().position(latLng)
            icon?.let {
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(it))
            }
            _googleMap?.addMarker(markerOption)?.let { x ->
                _subMarker.add(x)
            }
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun addMarkerOption(latLng: LatLng, icon: Int? = null): String? {
        val findBuff = _subMarker.find { x -> x.position == latLng }
        if (findBuff != null) {
            return "already add"
        }
        try {
            val markerOption = MarkerOptions().position(latLng)
            icon?.let {
                markerOption.icon(BitmapDescriptorFactory.fromResource(it))
            }
            _googleMap?.addMarker(markerOption)?.let { x ->
                _subMarker.add(x)
            }
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun addMarkerOption(latLng: LatLng, iconUrl: String): String? {
        val findBuff = _subMarker.find { x -> x.position == latLng }
        if (findBuff != null) {
            return "already add"
        }
        try {
            val markerOption = MarkerOptions().position(latLng)
            val icon = FImageUtils.urlToBitmap(iconUrl)
            markerOption.icon(icon)
            _googleMap?.addMarker(markerOption)?.let { x ->
                _subMarker.add(x)
            }
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun addMarkerCustomView(context: Context, latLng: LatLng, name: String, image: String, catchResource: Int): String? {
        val findBuff = _subMarker.find { x -> x.position == latLng }
        if (findBuff != null) {
            return "already add"
        }
        try {
            val markerOption = MarkerOptions().position(latLng)
            val inflater = LayoutInflater.from(context)
            val view: CustomClusterViewBinding = DataBindingUtil.inflate(inflater, R.layout.custom_cluster_view, null, false)
            view.tvName.text = name
            FGlideSupport.imageLoad(context, view.ivHmv, image, catchResource) {
                view.ivHmv.isVisible = true
                view.ivHmv.setImageDrawable(it)
                view.ivHmv.calculatePath(10F)
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(FImageUtils.getBitmapFromView(view.root)))
                _googleMap?.addMarker(markerOption)?.let { x ->
                    _subMarker.add(x)
                }
            }
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun addClusterMarkerOption(index: Int = 0, name: String = "", subTitle: String = "", latitude: Double = 0.0, longitude: Double = 0.0, image: String = "", catchResource: Int = 0): String? {
        return addClusterMarkerOption(MarkerClusterDataModel(index, name, subTitle, latitude, longitude, image, catchResource))
    }
    fun addClusterMarkerOption(data: MarkerClusterDataModel): String? {
        val findBuff = _clusterManager?.algorithm?.items?.find { it.index == data.index }
        if (findBuff != null) {
            return "already add"
        }
        _clusterManager?.addItem(data)
        return ""
    }
    fun removeClusterMarkerOption(index: Int) {
        val findBuff = _clusterManager?.algorithm?.items?.find { it.index == index }
        if (findBuff != null) {
            _clusterManager?.removeItem(findBuff)
        }
    }
    fun clearClusterMarkerOption() {
        _clusterManager?.clearItems()
    }
    // endregion

    // region set circle
    fun setCircleOption(lat: Double, lng: Double, radius: Double, strokeColor: Int, fillColor: Int): String? {
        return setCircleOption(LatLng(lat, lng), radius, strokeColor, fillColor)
    }
    fun setCircleOption(latLng: LatLng, radius: Double, strokeColor: Int, fillColor: Int, strokeWidth: Float = 0F): String? {
        if (_googleMap == null) return "map is null"
        _googleCircle?.remove()
        try {
            val circleOption = CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
                .strokeWidth(strokeWidth)
            _googleCircle = _googleMap?.addCircle(circleOption)
        } catch (e: Exception) {
            return e.message
        }
        return ""
    }
    fun setCircleOption(radius: Double, strokeColor: Int, fillColor: Int, strokeWidth: Float = 0F): String? {
        if (_googleMap == null) return "map is null"
        _googleCircle?.remove()
        try {
            val circleOption = CircleOptions()
                .center(LatLng(_latitude, _longitude))
                .radius(radius)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
                .strokeWidth(strokeWidth)
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
            _googleMarker?.position = latLng
            _googleCircle?.center = latLng
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
    fun setCustomLocal(lang: String, script: String? = null, region: String? = null) {
        if (lang.isEmpty()) {
            return
        }
        _customLocale = Locale.Builder().setLanguage(lang).apply {
            if (!script.isNullOrEmpty()) {
                setScript(script)
            }
            if (!region.isNullOrEmpty()) {
                setRegion(region)
            }
        }.build()
    }
    fun getLocale(): Locale {
        return _customLocale ?: Locale.getDefault()
    }
    fun getLanguage(): String {
        return _customLocale?.language ?: Locale.getDefault().language
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
    fun searchGeocoder(context: Context, lat: Double, lng: Double, locale: Locale = getLocale(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
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
    fun searchGeocoder(context: Context, latLng: LatLng, locale: Locale = getLocale(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
        searchGeocoder(context, latLng.latitude, latLng.longitude, locale, callback, error)
    }
    fun searchGeocoder(context: Context, locale: Locale = getLocale(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
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
    fun searchGeocoder(context: Context, placeName: String, locale: Locale = getLocale(), callback: (MutableList<Address>) -> Unit, error: (String?) -> Unit) {
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

    interface IMarkerClusterClickListener {
        fun onMarkerClickListener(clusterItem: MarkerClusterDataModel)
        fun onClusterClickListener(cluster: Cluster<MarkerClusterDataModel>)
    }
    data class MarkerClusterDataModel(
        var index: Int = 0,
        var name: String = "",
        var subName: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var image: String = "",
        var catchResource: Int = 0,
    ): ClusterItem {
        override fun getPosition() = LatLng(latitude, longitude)
        override fun getTitle() = ""
        override fun getSnippet() = subName.takeIf { it.isNotEmpty() }
    }

    class FClusterRenderer(val context: Context, val map: GoogleMap, clusterManager: ClusterManager<MarkerClusterDataModel>, val markerClusterClickListener: IMarkerClusterClickListener): DefaultClusterRenderer<MarkerClusterDataModel>(context, map, clusterManager) {
        override fun onBeforeClusterItemRendered(item: MarkerClusterDataModel, markerOptions: MarkerOptions) {
            val inflater = LayoutInflater.from(context)
            val view: CustomClusterViewBinding = DataBindingUtil.inflate(inflater, R.layout.custom_cluster_view, null, false)
            // 아 이거 왜 안됨
//            view.dataContext = item
            view.tvName.text = item.name
            if (item.image.isNotEmpty() || item.catchResource != 0) {
                FGlideSupport.imageLoad(context, view.ivHmv, item.image, item.catchResource) {
                    view.ivHmv.isVisible = true
                    view.ivHmv.setImageDrawable(it)
                    view.ivHmv.calculatePath(10F)
                    getMarker(item)?.setIcon(BitmapDescriptorFactory.fromBitmap(FImageUtils.getBitmapFromView(view.root)))
                }
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(FImageUtils.getBitmapFromView(view.root)))
            } else {
                view.ivDefImage.isVisible = true
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(FImageUtils.getBitmapFromView(view.root)))
            }
            map.setOnMarkerClickListener { x ->
                val markerItem = getClusterItem(x)
                if (markerItem == null) {
                    val cluster = getCluster(x)
                    if (cluster != null) {
                        markerClusterClickListener.onClusterClickListener(cluster)
                    }
                } else {
                    markerClusterClickListener.onMarkerClickListener(markerItem)
                }
                true
            }
        }
        override fun onBeforeClusterRendered(cluster: Cluster<MarkerClusterDataModel>, markerOptions: MarkerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions)
        }
    }
}

typealias MapOptions = EnumSet<FGooglePlaySupport.MapOption>