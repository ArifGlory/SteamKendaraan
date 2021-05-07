package com.tapisdev.mysteam.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.activity.steam.DetailSteamActivity
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
    val myDB = FirebaseFirestore.getInstance()
    val fasilitasRef = myDB.collection("fasilitas")

    lateinit var pDialogLoading : SweetAlertDialog

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val nf = NumberFormat.getNumberInstance(Locale.GERMAN)
        val df = nf as DecimalFormat
        mUserPref = UserPreference(holder.view.lineFasilitas.context)
        pDialogLoading = SweetAlertDialog(holder.view.lineFasilitas.context, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        holder.view.tvNamaFasilitas.text = list?.get(position)?.nama_fasilitas

        if (!list?.get(position)?.harga.equals("")){
            var harga : Int
            harga = list?.get(position)?.harga.toInt()
            holder.view.tvHarga.text = "Rp. "+df.format(harga)
        }else{
            holder.view.tvHarga.text = "-"
        }

        if (mUserPref.getJenisUser().equals("admin") || mUserPref.getJenisUser().equals("steam")){
            holder.view.ivDeleteFasilitas.visibility = View.VISIBLE
        }
        holder.view.ivDeleteFasilitas.setOnClickListener {
            SweetAlertDialog(holder.view.lineFasilitas.context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin menghapus ini ?")
                .setContentText("Data yang sudah dihapus tidak bisa dikembalikan")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    pDialogLoading.show()
                    fasilitasRef.document(list?.get(position)?.id_fasilitas).delete().addOnSuccessListener {
                        pDialogLoading.dismiss()
                        Toasty.success(holder.view.lineFasilitas.context, "Data berhasil dihapus", Toast.LENGTH_LONG, true).show()

                        if (holder.view.lineFasilitas.context is DetailSteamActivity){
                            (holder.view.lineFasilitas.context as DetailSteamActivity).refreshList()
                        }

                        Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
                    }.addOnFailureListener {
                            e ->
                        pDialogLoading.dismiss()
                        Toasty.error(holder.view.lineFasilitas.context, "terjadi kesalahan "+e, Toast.LENGTH_LONG, true).show()
                        Log.w("deleteDoc", "Error deleting document", e)
                    }

                }
                .setCancelButton(
                    "Tidak"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
        }



        holder.view.lineFasilitas.setOnClickListener {
            Log.d("adapterIsi",""+list.get(position).toString())


        }

    }


    class Holder(val view: View) : RecyclerView.ViewHolder(view)

}