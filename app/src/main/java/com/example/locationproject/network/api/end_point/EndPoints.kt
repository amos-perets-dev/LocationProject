package com.example.locationproject.network.api.end_point

import android.location.Location
import com.example.locationproject.BuildConfig

class EndPoints : IEndPoints {

    private val PLACES_PATH =
        "nearbysearch/json?location=%1\$s,%2\$s&radius=500&language=en&type=point_of_interest&key=${BuildConfig.GOOLE_KEY}"

    override fun getPlacesUrl(location: Location): String = String.format(PLACES_PATH, location.latitude, location.longitude)

}