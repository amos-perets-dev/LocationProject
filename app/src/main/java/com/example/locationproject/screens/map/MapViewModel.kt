package com.example.locationproject.screens.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locationproject.manager.location.ILocationManager
import com.example.locationproject.manager.location.LocationManager
import com.example.locationproject.manager.location.data.DataLocationResult
import com.example.locationproject.manager.map.IMapManager
import com.example.locationproject.repository.IPlacesRepository
import com.microsoft.maps.MapElementLayer
import io.reactivex.Observable

class MapViewModel(
    private val locationManager: ILocationManager,
    private val placesRepository: IPlacesRepository,
    mapManager: IMapManager

) : ViewModel() {


    val pinLayer = MutableLiveData<MapElementLayer>()

    init {

        pinLayer.postValue(mapManager.getPinLayer())
        locationManager.init()

    }


    fun getState(): Observable<DataLocationResult> {
        return locationManager.getStateChange()
    }

    /**
     * Get the current location from the [IPlacesRepository]
     *
     * @return [Observable][Location]
     */
    fun getCurrLocation(): Observable<Location> = placesRepository.getCurrLocation()

    /**
     * Get the active place from the [IPlacesRepository]
     *
     * @return [Observable] of the active place name
     */
    fun getActivePlace(): Observable<String>? = placesRepository.getActivePlace()

    fun onDestroy() {
        locationManager.onStop()
    }

    /**
     * Call when the user click on the dialog msg
     */
    fun onClickMsg() {
        locationManager.init()
    }

    /**
     * Call when the permission denied
     */
    fun permissionDenied() {
        locationManager.init()
    }

}