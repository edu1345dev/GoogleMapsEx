package com.example.googlemapsex

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.projectapp.PermissionsHelper
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationCallback: MyLocationCallback
    private val permissions =
            listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var mMap: GoogleMap
    private val sidney by lazy { findViewById<Button>(R.id.sidney_bt) }
    private val roma by lazy { findViewById<Button>(R.id.roma_bt) }
    private val saoPaulo by lazy { findViewById<Button>(R.id.saopaulo_bt) }
    private lateinit var permissionsHelper: PermissionsHelper
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        permissionsHelper = PermissionsHelper(this)
        permissionsHelper.requestAllPermission(permissions)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        val locationRequest = LocationRequest.create().apply {
            interval = 15000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = MyLocationCallback {
            updateCurrentLocation(it.lastLocation)
        }

        if (permissionsHelper.requestAllPermission(permissions)) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun updateCurrentLocation(lastLocation: Location) {
        currentLocation = lastLocation
        addMarker(LatLng(lastLocation.latitude, lastLocation.longitude), "Current")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        registerButtons()
    }

    private fun registerButtons() {
        sidney.setOnClickListener {
            addMarker(LatLng(-34.0, 151.0), "Sydney")
        }

        saoPaulo.setOnClickListener {
            addMarker(LatLng(-23.533773, -46.625290), "São Paulo")
        }

        roma.setOnClickListener {
            addMarker(LatLng(41.902782, 12.496366), "Roma")
        }
    }

    private fun addMarker(location: LatLng, name: String) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(location).title("Marker in $name"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}

class MyLocationCallback(private val callback: (LocationResult) -> Unit) : LocationCallback() {
    override fun onLocationResult(p0: LocationResult) {
        super.onLocationResult(p0)
        callback(p0)
    }
}



//val intentUri: Uri = Uri.parse("geo:0,0?q=267 Rua São Vicente, Arujá, São Paulo")
//val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
//mapIntent.setPackage("com.google.android.apps.maps")
//startActivity(mapIntent)