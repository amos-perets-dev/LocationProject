package com.example.locationproject.manager.places

import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.example.locationproject.manager.location.ILocationManager
import com.example.locationproject.network.api.PlacesApi
import com.example.locationproject.network.api.end_point.IEndPoints
import com.example.locationproject.network.data.PlacesDataResponse
import com.microsoft.maps.Geopoint
import com.microsoft.maps.MapIcon
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PlacesNetworkManager(
    private val placesApi: PlacesApi?,
    private val endPoints: IEndPoints
) : IPlacesNetworkManager {

    override fun getNearbyPlaces(location: Location): Single<List<PlacesDataResponse.PlaceData>>? {
        return placesApi
            ?.getNearbyPlaces(endPoints.getPlacesUrl(location))
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.map { response ->
                response.placesResults?.take(5)
            }
    }

}