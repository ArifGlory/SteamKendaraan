package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Rating(
    var nilai_rating : Int = 0,
    var komentar : String = "",
    var id_user : String = "",
    var id_pemilik : String = "",
    var id_steam : String = "",
    var id_rating : String = ""
) : Parcelable,java.io.Serializable