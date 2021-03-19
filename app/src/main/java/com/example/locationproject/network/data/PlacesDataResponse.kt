package com.example.locationproject.network.data

import com.google.gson.annotations.SerializedName

data class PlacesDataResponse(
    @SerializedName("results")
    val placesResults: List<PlaceData>?
) {

    data class PlaceData(
        @SerializedName("name")
        val placeName: String?,

        @SerializedName("place_id")
        val placeId: String?,

        @SerializedName("geometry")
        private val placeGeometry: PlaceGeometry?
    ) {

        fun getLocation(): PlaceGeometry.PlaceLocation? {
            return placeGeometry?.location
        }

        fun getLocationLat(): Double {
            return placeGeometry?.location?.lat ?: 0.0
        }

        fun getLocationLng(): Double {
            return placeGeometry?.location?.lng ?: 0.0
        }

        data class PlaceGeometry(
            @SerializedName("location")
            val location: PlaceLocation?
        ) {

            data class PlaceLocation(
                @SerializedName("lat")
                val lat: Double?,

                @SerializedName("lng")
                val lng: Double?
            )

        }

    }

}
