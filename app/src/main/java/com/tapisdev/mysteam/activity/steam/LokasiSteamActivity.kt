package com.tapisdev.mysteam.activity.steam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Info
import com.akexorcist.googledirection.model.Step
import com.akexorcist.googledirection.util.DirectionConverter
import com.akexorcist.googledirection.util.execute

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_lokasi_steam.*

class LokasiSteamActivity : BaseActivity(), OnMapReadyCallback,PermissionHelper.PermissionListener,LocationListener {

    private lateinit var mMap: GoogleMap
    lateinit var i : Intent
    lateinit var lm : LocationManager
    lateinit var steam : Steam
    lateinit var  permissionHelper : PermissionHelper

    var lat  = 0.0
    var lon  = 0.0
    var myLat  = 0.0
    var myLon  = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lokasi_steam)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        lm = (getSystemService(LOCATION_SERVICE) as LocationManager?)!!
        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)


        i = intent
        steam = i.getSerializableExtra("steam") as Steam
        lat = steam.lat.toDouble()
        lon = steam.lon.toDouble()

        rlRute.setOnClickListener {
            if (myLat != 0.0){
                showLoading(this)
                GoogleDirection.withServerKey("AIzaSyBYR_9WSimaGMFhwRyTSy25DKPrSnl97uc")
                    .from(LatLng(myLat, myLon))
                    .to(LatLng(lat,lon))
                    .transportMode(TransportMode.DRIVING)
                    .execute(
                        onDirectionSuccess = { direction: Direction? ->
                            dismissLoading()
                            if(direction!!.isOK()) {
                                // Do something
                                //showSuccessMessage("direction berhasil")
                                //megmabil data direction dari API
                                val route  = direction.routeList.get(0)
                                val leg  = route.legList.get(0)
                                var listStep : List<Step>
                                listStep = leg.stepList
                                var pointList : ArrayList<LatLng>
                                pointList = leg.directionPoint

                                //mengetahui jarak dan durasi perjalanan
                                var distanceInfo : Info
                                var durationInfo : Info
                                distanceInfo = leg.distance
                                durationInfo = leg.duration
                                tvJarak.setText(""+distanceInfo.text+" , dalam "+durationInfo.text)

                                //memulai menggambar rute di maps
                                var directionPositionList : ArrayList<LatLng>
                                directionPositionList = leg.directionPoint

                                var polylineOptions : PolylineOptions
                                polylineOptions = DirectionConverter.createPolyline(this,directionPositionList,6,
                                    Color.GREEN)
                                mMap.addPolyline(polylineOptions)

                            } else {
                                // Do something
                                showErrorMessage("rute tidak data ditemukan")
                            }
                        },
                        onDirectionFailure = { t: Throwable ->
                            // Do something
                            dismissLoading()
                            showErrorMessage("gagal membuat rute, coba lagi nanti")
                        }
                    )

            }else{
                showInfoMessage("lokasi anda tidak dapat ditemukan, mohon aktifkan GPS anda")
                try {
                    // Request location updates
                    lm?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    Log.d("getLoc", "Security Exception, no location available")
                }
            }
        }

        permissionLocation()
        if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            // Call your Alert message
            showInfoMessage("gps tidak aktif, layanan ini memerlukan GPS untuk Aktif")
        }else{
            try {
                // Request location updates
                lm?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            } catch(ex: SecurityException) {
                Log.d("getLoc", "Security Exception, no location available")
                showErrorMessage("Lokasi anda tidak ditemukan, harap aktifkan GPS Anda")
            }
        }
        showSweetInfo("Untuk akurasi yang lebih baik, harap aktifkan GPS Anda")
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

        val lokasi = LatLng(lat, lon)
        val zoomLevel = 16.0f //This goes up to 21
        mMap.addMarker(MarkerOptions().position(lokasi).title("Lokasi Steam"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi,zoomLevel))
    }

    private fun permissionLocation() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        listPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //thetext.text = ("" + location.longitude + ":" + location.latitude)
            //showInfoMessage("lat : "+location.longitude+" | lon : "+location.longitude)
            myLat = location.latitude
            myLon = location.longitude
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onPermissionCheckDone() {
        if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            // Call your Alert message
            showInfoMessage("gps tidak aktif, layanan ini memerlukan GPS untuk Aktif")
        }else{
            try {
                // Request location updates
                lm?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
            } catch(ex: SecurityException) {
                Log.d("getLoc", "Security Exception, no location available")
                showErrorMessage("Lokasi anda tidak ditemukan, harap aktifkan GPS Anda")
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d("getLoc",""+location.latitude+" | "+location.toString())
        if (location == null) {
            showErrorMessage("Lokasi Kamu Tidak Dapat Ditemukan")
        } else {
            myLat = location.latitude
            myLon = location.longitude
            //showInfoMessage("lat :"+myLat + " | lon:"+myLon)

        }
    }


}
