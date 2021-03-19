package com.example.locationproject.network.api.end_point

import android.location.Location

interface IEndPoints {

    /**
     * Create get places end point
     * @param location - current location
     *
     * @return url
     */
    fun getPlacesUrl(location: Location): String

}