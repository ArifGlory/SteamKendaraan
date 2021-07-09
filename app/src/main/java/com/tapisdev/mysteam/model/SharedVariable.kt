package com.tapisdev.mysteam.model

import com.google.android.gms.maps.model.LatLng

class SharedVariable {

    companion object {
        lateinit var user : UserModel
        var centerLatLon : LatLng = LatLng(0.0,0.0)
        open var uniqJalurID = "-"
        open var graphNodeStatus = false

    }
}