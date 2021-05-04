package com.tapisdev.mysteam.activity.steam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.Fasilitas
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_add_fasilitas.*

class AddFasilitasActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var steam : Steam
    var TAG_SIMPAN = "simpanFasilitas"
    lateinit var fasilitas : Fasilitas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fasilitas)
        mUserPref = UserPreference(this)
        i = intent

        steam = i.getSerializableExtra("steam") as Steam

        btnSimpanFasilitas.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getName = edName.text.toString()
        var getHarga = edHarga.text.toString()

        if (getName.equals("") || getName.length == 0){
            getHarga = ""
        }
        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }
        else {
            fasilitas = Fasilitas(getName,getHarga,steam.id_pemilik,steam.id_steam,"")
            saveFasilitas()
        }
    }

    fun saveFasilitas(){
        showLoading(this)
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")

        fasilitasRef.document().set(fasilitas).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                dismissLoading()
                showLongSuccessMessage("Tambah Fasilitas Berhasil")
                onBackPressed()
            }else{
                dismissLoading()
                showLongErrorMessage("Error, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }
}
