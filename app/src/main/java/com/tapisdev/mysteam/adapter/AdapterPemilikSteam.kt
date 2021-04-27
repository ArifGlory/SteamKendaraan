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
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_pemilik_steam.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterPemilikSteam(private val list:ArrayList<UserModel>) : RecyclerView.Adapter<AdapterPemilikSteam.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_pemilik_steam,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        holder.view.tvNamaPemilik.text = list?.get(position)?.name
        holder.view.tvEmail.text =list?.get(position)?.email
        holder.view.tvPhone.text = list?.get(position)?.phone


        Glide.with(holder.view.ivFotoPemilik.context)
            .load(list?.get(position)?.foto)
            .into(holder.view.ivFotoPemilik)

        holder.view.lineSparepart.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())
            

        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}