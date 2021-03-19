package com.example.locationproject.network.api

import com.example.locationproject.network.data.PlacesDataResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Url

interface PlacesApi {

    /**
     * Call to get the places nearby the current location
     *
     * @return [Single][PlacesDataResponse]
     */
    @Headers("Content-Type:application/json", "charset:UTF-8")
    @GET
    fun getNearbyPlaces(@Url url: String): Single<PlacesDataResponse>

}