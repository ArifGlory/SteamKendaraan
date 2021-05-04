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
import com.tapisdev.mysteam.activity.steam.ListSteamActivity
import com.tapisdev.mysteam.adapter.AdapterSteam
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_home_pengguna.*
import kotlinx.android.synthetic.main.activity_home_pengguna.lineProfil
import kotlinx.android.synthetic.main.activity_home_pengguna.rvSteam
import kotlinx.android.synthetic.main.activity_home_steam.*

class HomePenggunaActivity : BaseActivity() {

    var TAG_GET_STEAM = "getSteam"
    lateinit var adapter: AdapterSteam

    var listSteam = ArrayList<Steam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_pengguna)
        mUserPref = UserPreference(this)
        adapter = AdapterSteam(listSteam)
        rvSteam.setHasFixedSize(true)
        rvSteam.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvSteam.adapter = adapter

        lineProfil.setOnClickListener {
            startActivity(Intent(this, ProfilPenggunaActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineSteam.setOnClickListener {
            startActivity(Intent(this, ListSteamActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        getDataSteam()
    }

    fun getDataSteam(){
        steamRef.limit(5).get().addOnSuccessListener { result ->
            listSteam.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var steam : Steam = document.toObject(Steam::class.java)
                steam.id_steam = document.id
                listSteam.add(steam)
            }
            if (listSteam.size == 0){
                av_steam.setAnimation(R.raw.empty_box)
                av_steam.playAnimation()
                av_steam.loop(false)
            }else{
                av_steam.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_STEAM,"err : "+exception.message)
        }
    }
}
