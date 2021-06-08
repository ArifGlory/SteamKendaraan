package com.tapisdev.mysteam.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.opengl.GLDebugHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.Booking
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_booking.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterBooking(private val list:ArrayList<Booking>) : RecyclerView.Adapter<AdapterBooking.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_booking,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        holder.view.tvNamaUser.text = list?.get(position)?.nama_user
        holder.view.tvJam.text =list?.get(position)?.jam

        Glide.with(holder.view.ivFotoUser.context)
            .load(list?.get(position)?.foto_user)
            .into(holder.view.ivFotoUser)

        holder.view.lineBooking.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())


        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}