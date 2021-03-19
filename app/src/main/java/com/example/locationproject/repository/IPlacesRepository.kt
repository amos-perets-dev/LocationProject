package com.example.locationproject.repository

import android.location.Location
import com.example.locationproject.network.data.PlacesDataResponse
import com.microsoft.maps.MapIcon
import io.reactivex.Observable

interface IPlacesRepository {

    /**
     * Set the places from the server
     */
    fun setLocationsData(points: List<PlacesDataResponse.PlaceData>)

    /**
     * Set the current location from the device
     */
    fun setLocationData(currLocation: Location)

    /**
     * Get the current location
     *
     * @return [Observable][Location]
     */
    fun getCurrLocation(): Observable<Location>

    /**
     * Set the active place if the user enters it
     */
    fun setActivePlace(placeID: String)

    /**
     * Get the active place
     *
     * @return [Observable] of the active place name
     */
    fun getActivePlace(): Observable<String>?
}