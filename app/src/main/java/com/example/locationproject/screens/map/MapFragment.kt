package com.example.locationproject.screens.map

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import com.example.locationproject.BuildConfig
import com.example.locationproject.R
import com.example.locationproject.manager.location.data.DataLocationResult
import com.example.locationproject.screens.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.microsoft.maps.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_map.view.*
import org.koin.android.ext.android.inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapFragment : BaseFragment() {

    private var mapView: MapView? = null
    private val viewModel by inject<MapViewModel>()

    override fun getLayout(): Int = R.layout.fragment_map

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.pinLayer.observe(viewLifecycleOwner, Observer { pinLayer ->
            mapView = MapView(
                requireContext(),
                MapRenderMode.VECTOR
            )

            mapView?.setCredentialsKey(BuildConfig.MAP_KEY)
            view.map_view.addView(mapView)
            mapView?.layers?.add(pinLayer)
            mapView?.onCreate(savedInstanceState)
        })

        val load = view.load

        compositeDisposable.add(
            viewModel.getState()
                .subscribe {
                    when (it) {
                        is DataLocationResult.ErrorMsg -> showMsg(view, it)
                        is DataLocationResult.LocationReady -> {
                            load.animate().alpha(0F).setDuration(1000).start()
                        }
                    }
                }
        )

        compositeDisposable.add(
            viewModel.getCurrLocation()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { currLocation ->
                    moveCamera(Geopoint(currLocation.latitude, currLocation.longitude))
                }
        )

        val activePlace = view.active_place

        viewModel.getActivePlace()
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { currActivePlace ->
                activePlace.text = currActivePlace

                animateView(activePlace, 1F)
                    ?.withEndAction {
                        animateView(activePlace, 0F)
                            ?.setStartDelay(5000)
                            ?.start()
                    }
                    ?.start()
            }?.let {
                compositeDisposable.add(
                    it

                )
            }


    }

    /**
     * Animate the banner view on the top screen
     */
    private fun animateView(view: AppCompatTextView, scale: Float): ViewPropertyAnimator? {
        return view
            .animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(500)
    }

    /**
     * Move the camera on the map according to the location
     */
    private fun moveCamera(currLocation: Geopoint) {
        mapView?.setScene(
            MapScene.createFromLocationAndZoomLevel(currLocation, 16.0),
            MapAnimationKind.DEFAULT
        )
    }

    /**
     * Show msg
     */
    private fun showMsg(view: View, locationResult: DataLocationResult) {

        if (locationResult is DataLocationResult.ErrorMsg.NetworkError) {

            val dialog = AlertDialog.Builder(context)
                .setMessage(locationResult.error)
                .setCancelable(false)
                .setPositiveButton(
                    R.string.dialog_general_ok_text
                ) { dialog, which ->
                    viewModel.onClickMsg()
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
            dialog
                .show()

            return
        }

        Snackbar.make(
            view,
            (locationResult as DataLocationResult.ErrorMsg).msg,
            Snackbar.LENGTH_LONG
        )
            .setAction("Action", null).show()

        if (locationResult is DataLocationResult.ErrorMsg.PermissionDenied){
            viewModel.permissionDenied()
        }

    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        mapView?.onDestroy()
        viewModel.onDestroy()
        super.onDestroyView()
    }
}