package com.tapisdev.mysteam.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.adapter.AdapterSteam
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_list_steam.*

class ResultActivity : BaseActivity() {

    lateinit var i : Intent
    var jenis = "none"
    var value = "none"

    var TAG_GET_STEAM = "getSteam"
    lateinit var adapter: AdapterSteam
    var listSteam = ArrayList<Steam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        mUserPref = UserPreference(this)
        adapter = AdapterSteam(listSteam)
        rvSteam.setHasFixedSize(true)
        rvSteam.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvSteam.adapter = adapter

        i = intent
        jenis = i.getStringExtra("jenis").toString()
        value = i.getStringExtra("value").toString()

        if (jenis.equals("search")){
            getDataSearchSteam()
        }
    }

    fun getDataSearchSteam(){
        steamRef.get().addOnSuccessListener { result ->
            listSteam.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var steam : Steam = document.toObject(Steam::class.java)
                steam.id_steam = document.id

                var nama = steam.nama_steam.toLowerCase()
                value = value.toLowerCase()

                if (nama.contains(value)){
                    listSteam.add(steam)
                }

            }
            if (listSteam.size == 0){
                animation_view_steam.setAnimation(R.raw.empty_box)
                animation_view_steam.playAnimation()
                animation_view_steam.loop(false)
            }else{
                animation_view_steam.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_STEAM,"err : "+exception.message)
        }
    }
}
