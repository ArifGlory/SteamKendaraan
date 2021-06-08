package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Booking(
    var tanggal : String = "",
    var jam : String = "",
    var nama_user : String = "",
    var foto_user : String = "",
    var nama_steam : String = "",
    var status_booking : String = "",
    var id_user : String = "",
    var id_pemilik : String = "",
    var id_steam : String = "",
    var id_booking : String = ""
) : Parcelable,java.io.Serializable