package com.example.locationproject.manager.places

import android.location.Location
import com.example.locationproject.network.data.PlacesDataResponse
import com.microsoft.maps.MapIcon
import io.reactivex.Single

interface IPlacesNetworkManager {

    /**
     * Get the places list[PlacesDataResponse.PlaceData] from the server
     *
     * @param location - the current location of the user
     */
    fun getNearbyPlaces(location : Location): Single<List<PlacesDataResponse.PlaceData>>?

}