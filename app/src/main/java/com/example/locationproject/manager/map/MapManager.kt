package com.example.locationproject.manager.map

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import com.example.locationproject.location_project.configuration.IMapProjectConfiguration
import com.example.locationproject.location_project.configuration.MapProjectConfiguration
import com.example.locationproject.manager.location.data.DataLocationResult
import com.example.locationproject.network.data.PlacesDataResponse
import com.microsoft.maps.*

class MapManager(
    private val pinLayer: MapElementLayer,
    private val myLocationIcon: Bitmap,
    private val place: Bitmap,
    private val mapProjectConfiguration: IMapProjectConfiguration,
    private val defIcon: Bitmap
    ) : IMapManager {

    private val pinLayerElements = pinLayer.elements

    override fun createPins(
        currentLocation: DataLocationResult.LocationDataLocation,
        nearbyPlacesList: List<PlacesDataResponse.PlaceData>
    ): List<PlacesDataResponse.PlaceData> {

        val indexPlacesInRange = calculateBearing(currentLocation, nearbyPlacesList)

        if (nearbyPlacesList.size + 1 != pinLayerElements.size()) {
            pinLayerElements.clear()
        }

        if (pinLayerElements.size() == 0) {
            pinLayerElements.add(
                createPin(
                    currentLocation.location.latitude,
                    currentLocation.location.longitude,
                    currentLocation.title,
                    true
                )
            )

            nearbyPlacesList.forEachIndexed { index, placeData ->
                val mapIcon = createPin(
                    placeData.getLocationLat(),
                    placeData.getLocationLng(),
                    placeData.placeName,
                    false
                )

                if (indexPlacesInRange.contains(index)) {
                    (mapIcon as MapIcon).image = MapImage(place)
                } else {
                    (mapIcon as MapIcon).image = MapImage(defIcon)
                }


                pinLayerElements.add(mapIcon)
            }

        } else {
            val myLocation = pinLayerElements.firstOrNull()
            if (myLocation != null) {
                val location = currentLocation.location
                (myLocation as MapIcon).location = Geopoint(location.latitude, location.longitude)
            }

            for ((index, mapIcon) in pinLayerElements.drop(1).withIndex()) {


                val mapIconCast = mapIcon as MapIcon

                val placeData = nearbyPlacesList[index]
                mapIconCast.location =
                    Geopoint(placeData.getLocationLat(), placeData.getLocationLng())
                mapIconCast.title = placeData.placeName ?: ""

                if (indexPlacesInRange.contains(index)) {
                    mapIconCast.image = MapImage(place)
                } else {
                    mapIconCast.image = MapImage(defIcon)
                }

            }
        }

        return nearbyPlacesList

    }

    /**
     * calculate the bearing between the current location to other point on the map
     */
    private fun calculateBearing(
        currentLocation: DataLocationResult.LocationDataLocation,
        nearbyPlacesList: List<PlacesDataResponse.PlaceData>
    ): ArrayList<Int> {
        val indexPlacesInRange = arrayListOf<Int>()
        val currLocation = currentLocation.location

        nearbyPlacesList.forEachIndexed { index, placeData ->

            val destinationLoc = Location("service Provider")
            destinationLoc.latitude = placeData.getLocationLat()
            destinationLoc.longitude = placeData.getLocationLng()

            var bearing = currLocation.bearingTo(destinationLoc)
            if (bearing < 0) bearing += 360

            if (bearing < mapProjectConfiguration.getLimitDegrees()) {

                indexPlacesInRange.add(index)
            }


        }
        return indexPlacesInRange
    }


    /**
     * Create pin on the map
     *
     * @param latitude - location
     * @param longitude - location
     * @param title - of the place
     * @param isMyLocation - if it is my location or place from the server
     */
    private fun createPin(
        latitude: Double,
        longitude: Double,
        title: String?,
        isMyLocation: Boolean
    ): MapElement {

        val pin = MapIcon()
        pin.title = title ?: ""
        pin.location =
            Geopoint(latitude, longitude)
        if (isMyLocation) {
            pin.image = MapImage(myLocationIcon)

        }
        return pin

    }

    override fun getPinLayer(): MapElementLayer {
        return pinLayer
    }

}