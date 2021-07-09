package com.tapisdev.mysteam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GraphNode(
    var nodes : String = "",
    var uniqRouteID : String = "",
    var bobot : String = ""
) : Parcelable,java.io.Serializable