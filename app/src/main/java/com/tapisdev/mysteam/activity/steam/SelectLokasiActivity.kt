package com.tapisdev.mysteam.activity.steam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.SharedVariable
import com.tapisdev.mysteam.model.UserPreference
import com.tapisdev.mysteam.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_select_lokasi.*
import java.util.ArrayList

class SelectLokasiActivity : BaseActivity(), OnMapReadyCallback,PermissionHelper.PermissionListener {

    private lateinit var mMap: GoogleMap
    lateinit var  permissionHelper : PermissionHelper
    var lat  = 0.0
    var lon  = 0.0
    lateinit var centerMapLatLon :LatLng

    var TAG_UPDATE = "updateUSER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_lokasi)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mUserPref = UserPreference(this)

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)


        permissionLocation()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            permissionLocation()
            Log.d("permission","not granted")
        }else{
            mMap.isMyLocationEnabled = true
        }

        tvSelectLocation.setOnClickListener {
            showSuccessMessage("Lokasi dipilih")
            centerMapLatLon = mMap.projection.visibleRegion.latLngBounds.center
            SharedVariable.centerLatLon= centerMapLatLon
            onBackPressed()
        }


    }

    private fun permissionLocation() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        listPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    override fun onPermissionCheckDone() {

    }
}
