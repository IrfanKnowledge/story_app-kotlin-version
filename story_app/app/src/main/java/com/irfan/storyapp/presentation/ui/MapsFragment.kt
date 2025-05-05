package com.irfan.storyapp.presentation.ui

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.irfan.storyapp.R
import com.irfan.storyapp.common.MyLogger
import com.irfan.storyapp.presentation.view_model.HomeViewModel

class MapsFragment : Fragment() {
    private lateinit var map: GoogleMap

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isIndoorLevelPickerEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        addManyMarker()
        setMapStyle()
    }

    private fun getMyLocation() {
        return if (ContextCompat.checkSelfPermission(
                this.requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun addManyMarker() {
        val viewModelHome: HomeViewModel by navGraphViewModels(R.id.main_navigation)
        val listStorySnapshot = viewModelHome.listStorySnapshot

        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        map.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )
        if (listStorySnapshot.isNullOrEmpty()) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))
        }

        listStorySnapshot?.let {
            MyLogger.d(TAG, "listStorySnapshot: $it")
            for (story in it) {
                val lat = story.lat ?: 0.0
                val lon = story.lon ?: 0.0
                val latLng = LatLng(lat, lon)
                map.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                )
                boundsBuilder.include(latLng)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
            if (!success) {
                MyLogger.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            MyLogger.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsFragment"
    }
}