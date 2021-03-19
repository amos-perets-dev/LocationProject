package com.example.locationproject.manager.location.data

import android.location.Location

sealed class DataLocationResult {
    data class LocationDataLocation(val location: Location, val title: String) :
        DataLocationResult() {
        override fun equals(other: Any?): Boolean {
            if (other == null) return false
            val otherLocationData = (other as LocationDataLocation).location

            return otherLocationData.latitude == location.latitude &&
                    otherLocationData.longitude == location.longitude
        }
    }

    object LocationReady : DataLocationResult()

    sealed class ErrorMsg(val msg: String) : DataLocationResult() {
        data class PermissionDenied(val error: String) : ErrorMsg(error)
        data class ProviderDisable(val error: String) : ErrorMsg(error)
        data class NetworkError(val error: String) : ErrorMsg(error)
    }

}