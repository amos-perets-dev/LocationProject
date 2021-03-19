package com.example.locationproject.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.locationproject.repository.IPlacesRepository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import org.koin.java.KoinJavaComponent.inject

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val placesRepository: IPlacesRepository by inject(IPlacesRepository::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            val requestId = geofencingEvent.triggeringGeofences.firstOrNull()?.requestId


            if (requestId != null) {
                placesRepository.setActivePlace(requestId)
            }

        }
    }
}