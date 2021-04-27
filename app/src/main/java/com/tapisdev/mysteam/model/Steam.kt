package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Steam(
    var nama_steam : String = "",
    var alamat : String = "",
    var foto : String = "",
    var lat : String = "",
    var lon : String = "",
    var jenis_kendaraan : String = "",
    var id_pemilik : String = "",
    var id_steam : String = ""
) : Parcelable,java.io.Serializable