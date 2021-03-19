package com.example.locationproject.repository

import android.location.Location
import android.util.Log
import com.example.locationproject.location_project.configuration.IMapProjectConfiguration
import com.example.locationproject.network.data.PlacesDataResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class PlacesRepository(
    private val enterPlaceText: String
) : IPlacesRepository {

    private val currLocation = PublishSubject.create<Location>()
    private val activePlace = PublishSubject.create<String>()
    private val locations = HashMap<String, String>()

    override fun setLocationsData(points: List<PlacesDataResponse.PlaceData>) {
        points.forEach { placeData ->
            locations[placeData.placeId ?: ""] = placeData.placeName ?: ""
        }
    }

    override fun setLocationData(currLocation: Location) {
        this.currLocation.onNext(currLocation)
    }

    override fun getCurrLocation(): Observable<Location> {
        return currLocation.hide()
            .subscribeOn(Schedulers.io())
    }

    override fun setActivePlace(placeID: String) {

        val name = locations[placeID] ?: ""
        Log.d("TEST_GAME", "PlacesRepository setActivePlace name: $name")

        activePlace.onNext(name)
    }


    override fun getActivePlace(): Observable<String>? {
        return activePlace
            .hide()
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .map { place ->
                "$enterPlaceText $place"
            }
    }


}