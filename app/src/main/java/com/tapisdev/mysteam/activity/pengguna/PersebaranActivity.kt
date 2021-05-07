package com.tapisdev.mysteam.activity.pengguna

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.adapter.AdapterSteam
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import com.tapisdev.mysteam.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_list_steam.*

class PersebaranActivity : BaseActivity(), OnMapReadyCallback,PermissionHelper.PermissionListener {

    private lateinit var mMap: GoogleMap
    lateinit var  permissionHelper : PermissionHelper

    var TAG_GET_STEAM = "getSteam"
    lateinit var adapter: AdapterSteam

    var listSteam = ArrayList<Steam>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_persebaran)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mUserPref = UserPreference(this)
        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)
        adapter = AdapterSteam(listSteam)

        permissionLocation()
        getDataSteam()
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
    }

    private fun permissionLocation() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        listPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    fun getDataSteam(){
        steamRef.get().addOnSuccessListener { result ->
            listSteam.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var steam : Steam = document.toObject(Steam::class.java)
                steam.id_steam = document.id
                listSteam.add(steam)
            }
            adapter.notifyDataSetChanged()
            if (listSteam.size == 0){
                showInfoMessage("Belum ada data Steam Kendaraan")
            }else{
               setMarkers()
            }


        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_STEAM,"err : "+exception.message)
        }
    }

    fun setMarkers(){
        for (i in 0 until listSteam.size){
            var lat = listSteam.get(i).lat.toDouble()
            var lon = listSteam.get(i).lon.toDouble()
            var lokasi = LatLng(lat,lon)

            if (i != listSteam.size -1){
                mMap.addMarker(MarkerOptions().position(lokasi).title(listSteam.get(i).nama_steam))
            }else{
                mMap.addMarker(MarkerOptions().position(lokasi).title(listSteam.get(i).nama_steam))
                val zoomLevel = 10.0f //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi,zoomLevel))
            }
        }
    }

    override fun onPermissionCheckDone() {

    }
}
