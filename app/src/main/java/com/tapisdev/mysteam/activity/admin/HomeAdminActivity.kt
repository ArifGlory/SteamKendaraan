package com.tapisdev.mysteam.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.MainActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.activity.pengguna.ProfilPenggunaActivity
import com.tapisdev.mysteam.activity.steam.ListSteamActivity
import com.tapisdev.mysteam.adapter.AdapterSteam
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_home_admin.*
import kotlinx.android.synthetic.main.activity_home_pengguna.*

class HomeAdminActivity : BaseActivity() {

    var TAG_GET_STEAM = "getSteam"
    lateinit var adapter: AdapterSteam

    var listSteam = ArrayList<Steam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)
        mUserPref = UserPreference(this)
        adapter = AdapterSteam(listSteam)
        rvSteamAdmin.setHasFixedSize(true)
        rvSteamAdmin.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvSteamAdmin.adapter = adapter

        lineAddPemilik.setOnClickListener {
            startActivity(Intent(this, AddPemilikSteamActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        linePemilik.setOnClickListener {
            startActivity(Intent(this, ListPemilikActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineProfilAdmin.setOnClickListener {
            startActivity(Intent(this, ProfilPenggunaActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineSemuaSteam.setOnClickListener {
            startActivity(Intent(this, ListSteamActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineLogout.setOnClickListener {
            logout()
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
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
