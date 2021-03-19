package com.example.locationproject.manager.map

import com.example.locationproject.manager.location.data.DataLocationResult
import com.example.locationproject.network.data.PlacesDataResponse
import com.microsoft.maps.MapElementLayer

interface IMapManager {

    /**
     * Get the pin layer of the map
     */
    fun getPinLayer(): MapElementLayer

    /**
     * Create pins on the map
     */
   fun createPins(
       currentLocation: DataLocationResult.LocationDataLocation,
       nearbyPlacesList: List<PlacesDataResponse.PlaceData>
    ): List<PlacesDataResponse.PlaceData>
}