package com.example.locationproject.location_project

import android.content.Context
import com.example.locationproject.R
import com.example.locationproject.location_project.configuration.IMapProjectConfiguration
import com.example.locationproject.location_project.configuration.MapProjectConfiguration
import com.example.locationproject.network.base.BaseNetworkManager
import com.example.locationproject.network.base.IBaseNetworkManager
import com.example.locationproject.network.api.PlacesApi
import com.example.locationproject.manager.location.ILocationManager
import com.example.locationproject.util.IPermissionsUtil
import com.example.locationproject.manager.location.LocationManager
import com.example.locationproject.manager.map.IMapManager
import com.example.locationproject.manager.map.MapManager
import com.example.locationproject.manager.places.IPlacesNetworkManager
import com.example.locationproject.manager.places.PlacesNetworkManager
import com.example.locationproject.network.api.end_point.EndPoints
import com.example.locationproject.network.api.end_point.IEndPoints
import com.example.locationproject.network.error.HandleNetworkError
import com.example.locationproject.network.error.IHandleNetworkError
import com.example.locationproject.repository.IPlacesRepository
import com.example.locationproject.repository.PlacesRepository
import com.example.locationproject.util.PermissionsUtil
import com.example.locationproject.screens.map.MapViewModel
import com.example.locationproject.util.IconUtil
import com.google.android.gms.location.LocationServices
import com.microsoft.maps.MapElementLayer
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

class ModulesMap {

    /**
     * Create modules APP
     */
    fun createModules(context: Context): List<Module> {

        val applicationModule = createApplicationModule()
        val placesRepositoryModule = createPlacesRepository(context)
        val placesNetworkMangerModule = createPlacesNetworkManger()
        val locationMangerModule = createLocationManger(context)
        val mapMangerModule = createMapManger(context)

        val mapModule = createMapModule()

        return listOf(
            applicationModule,
            placesRepositoryModule,
            locationMangerModule,
            mapMangerModule,
            placesNetworkMangerModule,
            mapModule
        )

    }

    private fun createApplicationModule(): Module {

        return module {
            factory<IPermissionsUtil> { PermissionsUtil() }
            factory<IHandleNetworkError> { HandleNetworkError() }
            factory<IEndPoints> { EndPoints() }
            factory<IBaseNetworkManager> { BaseNetworkManager() }
            single<IMapProjectConfiguration> { MapProjectConfiguration() }

        }

    }


    private fun createMapModule(): Module {
        return module {
            viewModel { MapViewModel(get(), get(), get()) }
        }

    }

    private fun createLocationManger(context: Context): Module {
        return module {
            single<ILocationManager> {

                LocationManager(
                    context,
                    get<IPermissionsUtil>(),
                    LocationServices.getFusedLocationProviderClient(context),
                    get<IPlacesNetworkManager>(),
                    get<IPlacesRepository>(),
                    get<IHandleNetworkError>(),
                    LocationServices.getGeofencingClient(context),
                    context.getString(R.string.map_screen_my_location_pin_title_text),
                    get(),
                    get()
                )
            }
        }

    }

    private fun createMapManger(context: Context): Module {
        return module {
            single<IMapManager> {

                val iconUtil = IconUtil()
                MapManager(
                    MapElementLayer(),
                    iconUtil.drawableToBitmap(R.drawable.ic_placeholder, context),
                    iconUtil.drawableToBitmap(R.drawable.ic_place, context),
                    get()
                )
            }
        }

    }

    private fun createPlacesNetworkManger(): Module {
        return module {
            factory<IPlacesNetworkManager> {
                PlacesNetworkManager(
                    get<IBaseNetworkManager>()
                        .buildRetrofit()
                        ?.create(PlacesApi::class.java),
                    get<IEndPoints>()
                )
            }
        }

    }

    private fun createPlacesRepository(context: Context): Module {
        return module {
            single<IPlacesRepository> {
                PlacesRepository(
                    context.getString(R.string.map_screen_msg_enter_place_text)
                )
            }

        }

    }


}
