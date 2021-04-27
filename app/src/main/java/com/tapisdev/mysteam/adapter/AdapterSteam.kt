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
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_pemilik_steam.view.*
import kotlinx.android.synthetic.main.row_steam.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterSteam(private val list:ArrayList<Steam>) : RecyclerView.Adapter<AdapterSteam.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_steam,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        holder.view.tvNamaSteam.text = list?.get(position)?.nama_steam
        holder.view.tvAlamat.text =list?.get(position)?.alamat


        Glide.with(holder.view.ivSteam.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivSteam)

        holder.view.lineSteam.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())


        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}