package com.tapisdev.mysteam.activity.steam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.adapter.AdapterFasilitas
import com.tapisdev.mysteam.model.Fasilitas
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_detail_steam.*
import java.io.Serializable

class DetailSteamActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var steam : Steam

    var TAG_GET_Fasilitas = "getFasilitas"
    lateinit var adapter: AdapterFasilitas

    var listFasilitas = ArrayList<Fasilitas>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_steam)
        mUserPref = UserPreference(this)
        i = intent

        steam = i.getSerializableExtra("steam") as Steam

        adapter = AdapterFasilitas(listFasilitas)
        rvFasilitas.setHasFixedSize(true)
        rvFasilitas.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvFasilitas.adapter = adapter


        ivBack.setOnClickListener {
            onBackPressed()
        }
        cvCekLokasi.setOnClickListener {
            val i = Intent(this,LokasiSteamActivity::class.java)
            i.putExtra("steam",steam as Serializable)
            startActivity(i)
        }
        cvUbah.setOnClickListener {
            val i = Intent(this,EditSteamActivity::class.java)
            i.putExtra("steam",steam as Serializable)
            startActivity(i)
        }
        cvHapus.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin menghapus ini ?")
                .setContentText("Data yang sudah dihapus tidak bisa dikembalikan")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    showLoading(this)
                    steamRef.document(steam.id_steam).delete().addOnSuccessListener {
                        dismissLoading()
                        showSuccessMessage("Data berhasil dihapus")
                        onBackPressed()
                        Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
                    }.addOnFailureListener {
                            e ->
                        dismissLoading()
                        showErrorMessage("terjadi kesalahan "+e)
                        Log.w("deleteDoc", "Error deleting document", e)
                    }

                }
                .setCancelButton(
                    "Tidak"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
        }
        ivAddFasilitas.setOnClickListener {
            val i = Intent(this,AddFasilitasActivity::class.java)
            i.putExtra("steam",steam as Serializable)
            startActivity(i)
        }

        updateUI()
        getDataFasilitas()
    }

    fun updateUI(){
        if (mUserPref.getJenisUser().equals("admin") || steam.id_pemilik.equals(auth.currentUser?.uid)){
            lineSetting.visibility = View.VISIBLE
            ivAddFasilitas.visibility = View.VISIBLE
        }
        tvNamaSteam.setText(steam.nama_steam)
        tvAlamat.setText(steam.alamat)
        tvJenisKendaraan.setText(steam.jenis_kendaraan)
        Glide.with(this)
            .load(steam.foto)
            .into(ivSteam)
    }

    fun getDataFasilitas(){
        fasilitasRef.get().addOnSuccessListener { result ->
            listFasilitas.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var fasilitas : Fasilitas = document.toObject(Fasilitas::class.java)
                fasilitas.id_fasilitas = document.id
                if (fasilitas.id_steam.equals(steam.id_steam)){
                    listFasilitas.add(fasilitas)
                }
            }

            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_Fasilitas,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataFasilitas()
    }
}
