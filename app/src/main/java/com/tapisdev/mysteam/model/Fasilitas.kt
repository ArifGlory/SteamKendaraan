package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Fasilitas(
    var nama_fasilitas : String = "",
    var harga : String = "",
    var id_pemilik : String = "",
    var id_steam : String = "",
    var id_fasilitas : String = ""
) : Parcelable,java.io.Serializable