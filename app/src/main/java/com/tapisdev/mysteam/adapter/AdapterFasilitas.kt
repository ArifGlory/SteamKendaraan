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
import com.tapisdev.mysteam.model.Fasilitas
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.row_fasilitas.view.*
import java.io.Serializable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterFasilitas(private val list:ArrayList<Fasilitas>) : RecyclerView.Adapter<AdapterFasilitas.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.row_fasilitas,parent,false))
    }

    override fun getItemCount(): Int = list?.size
    lateinit var mUserPref : UserPreference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat

        holder.view.tvNamaFasilitas.text = list?.get(position)?.nama_fasilitas

        if (!list?.get(position)?.harga.equals("")){
            var harga : Int
            harga = list?.get(position)?.harga.toInt()
            holder.view.tvHarga.text = "Rp. "+df.format(harga)
        }else{
            holder.view.tvHarga.text = "-"
        }



        holder.view.lineFasilitas.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())


        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}