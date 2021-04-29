package com.tapisdev.mysteam.model

import com.google.android.gms.maps.model.LatLng

class SharedVariable {
    //open var selectedIdPenyedia = "-"

    companion object {
        lateinit var user : UserModel
        var centerLatLon : LatLng = LatLng(0.0,0.0)

    }
}