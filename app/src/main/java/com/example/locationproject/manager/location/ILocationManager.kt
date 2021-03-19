package com.example.locationproject.manager.location

import com.example.locationproject.manager.location.data.DataLocationResult
import io.reactivex.Observable

interface ILocationManager {

    /**
     * Get the state of the location result
     *
     * @return [Observable][DataLocationResult]
     */
    fun getStateChange(): Observable<DataLocationResult>

    /**
     * Call when need ti init the location and the places
     */
    fun init()

    fun onStop()
}