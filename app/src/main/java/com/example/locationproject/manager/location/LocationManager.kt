package com.example.locationproject.manager.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import com.example.locationproject.reciver.GeofenceBroadcastReceiver
import com.example.locationproject.R
import com.example.locationproject.location_project.configuration.IMapProjectConfiguration
import com.example.locationproject.manager.location.data.DataLocationResult
import com.example.locationproject.manager.map.IMapManager
import com.example.locationproject.manager.places.IPlacesNetworkManager
import com.example.locationproject.network.data.PlacesDataResponse
import com.example.locationproject.network.error.IHandleNetworkError
import com.example.locationproject.repository.IPlacesRepository
import com.example.locationproject.util.IPermissionsUtil
import com.google.android.gms.location.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject


class LocationManager(
    private val context: Context,
    private val permissionsUtil: IPermissionsUtil,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val placesNetworkManager: IPlacesNetworkManager,
    private val placesRepository: IPlacesRepository,
    private val handleNetworkError: IHandleNetworkError,
    private val geofencingClient: GeofencingClient,
    private val myLocationTitle: String,
    private val mapManager: IMapManager,
    private val mapProjectConfiguration: IMapProjectConfiguration
) : ILocationManager {

    private var heading = 0F

    private val compositeDisposable = CompositeDisposable()

    private val locationStateChanges = BehaviorSubject.create<DataLocationResult>()

    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun init() {
        onStop()

        openLocationPermissionDialog()

    }

    private fun openLocationPermissionDialog() {

        compositeDisposable.add(
            permissionsUtil
                .isPermissionGranted(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .subscribe({ isPermissionGranted ->

                    if (isPermissionGranted.not()) {
                        notifyStateChange(
                            DataLocationResult.ErrorMsg.PermissionDenied(
                                context.getString(R.string.map_screen_show_msg_permission_denied_text)
                            )
                        )
                        return@subscribe
                    }

                    Log.d("TEST_GAME", "locationManager?.requestLocationUpdates")

                    requestLocationUpdates()

                }, { error ->
                    Log.d("TEST_GAME", "subscribe error: ${error.message}")
                })
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {

        fetchPlacesByLocation()

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) return@addOnSuccessListener
                Log.d(
                    "TEST_GAME",
                    "addOnSuccessListener: ${location?.latitude}, accuracy: ${location?.accuracy}"
                )

                notifyLocationChange(location)

            }

        locationRequest = LocationRequest.create();
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest?.interval = 0

        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                val locationUnAvailable = locationAvailability.isLocationAvailable.not()

                if (locationUnAvailable) {
                    notifyStateChange(
                        DataLocationResult.ErrorMsg.ProviderDisable(
                            context.getString(R.string.map_screen_show_msg_gps_provider_text)
                        )
                    )
                }
            }

            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }

                for (location in locationResult.locations) {
                    if (location != null) {
                        notifyLocationChange(location)
                        break
                    }
                }
            }
        }

        if (locationRequest != null && locationCallback != null) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }


    }


    private fun fetchPlacesByLocation() {

        val myLocation = getStateChange()
            .filter { it is DataLocationResult.LocationDataLocation }
            .map { it as DataLocationResult.LocationDataLocation }


        val nearbyPlaces =
            myLocation
                .distinctUntilChanged()
                .flatMap { locationData ->
                    placesNetworkManager
                        .getNearbyPlaces(locationData.location)
                        ?.toObservable()
                }
                ?.doOnNext { placesData ->
                    placesRepository.setLocationsData(placesData)
                }

        val pinMyLocation = myLocation
            ?.doOnNext { locationData ->
                placesRepository.setLocationData(locationData.location)
            }

        compositeDisposable.add(
            Observable.combineLatest<DataLocationResult.LocationDataLocation, List<PlacesDataResponse.PlaceData>, List<PlacesDataResponse.PlaceData>>(
                pinMyLocation,
                nearbyPlaces,
                BiFunction { currentLocation, nearbyPlacesList ->
                    return@BiFunction mapManager.createPins(currentLocation, nearbyPlacesList)
                })
                .doOnNext { notifyStateChange(DataLocationResult.LocationReady) }
                .subscribe((this::createGeofencingRequest), {
                    notifyStateChange(
                        DataLocationResult.ErrorMsg.NetworkError(
                            context.getString(handleNetworkError.generateErrorID(throwable = it)) + "${it.message}"
                        )
                    )
                })
        )
    }

    private fun createGeofenceList(nearbyPlacesList: List<PlacesDataResponse.PlaceData>): ArrayList<Geofence> {

        val list = arrayListOf<Geofence>()

        nearbyPlacesList.forEach { placeData ->
            val geofence = Geofence.Builder()
                .setRequestId(placeData.placeId ?: "")
                .setCircularRegion(
                    placeData.getLocationLat(),
                    placeData.getLocationLng(),
                    mapProjectConfiguration.getRadiusPlace()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(5000)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT or
                            Geofence.GEOFENCE_TRANSITION_DWELL
                )
                .build()
            list.add(geofence)
        }

        return list

    }

    @SuppressLint("MissingPermission")
    private fun createGeofencingRequest(nearbyPlacesList: List<PlacesDataResponse.PlaceData>) {
        val geofenceList = createGeofenceList(nearbyPlacesList)

        val geofencingRequest = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL or GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()

        geofencingClient.removeGeofences(geofencePendingIntent)
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)

    }

    /**
     * Notify the state change
     */
    private fun notifyStateChange(dataLocationResult: DataLocationResult) {
        locationStateChanges.onNext(dataLocationResult)
    }

    /**
     * Notify when the current location change
     */
    private fun notifyLocationChange(location: Location) {
        notifyStateChange(
            DataLocationResult.LocationDataLocation(
                location, myLocationTitle
            )
        )
    }

    override fun getStateChange(): Observable<DataLocationResult> {
        return locationStateChanges.hide()
            .subscribeOn(Schedulers.io())
    }

    override fun onStop() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        compositeDisposable.clear()
        geofencingClient.removeGeofences(geofencePendingIntent)

    }


}