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
import com.tapisdev.mysteam.activity.pengguna.BookingActivity
import com.tapisdev.mysteam.model.Booking
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_booking_pengguna.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterBookingPengguna(private val list:ArrayList<Booking>) : RecyclerView.Adapter<AdapterBookingPengguna.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_booking_pengguna,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        holder.view.tvNamaSteam.text = list?.get(position)?.nama_steam
        holder.view.tvJam.text =list?.get(position)?.jam

        var id_steam = list?.get(position).id_steam
        Log.d("ABP","id steamnya : "+id_steam)

        holder.view.lineBookingPengguna.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            val i = Intent(holder.view.lineBookingPengguna.context, BookingActivity::class.java)
            i.putExtra("id_steam",id_steam)
            holder.view.lineBookingPengguna.context.startActivity(i)

        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}