package com.tapisdev.mysteam.activity.steam

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
import kotlinx.android.synthetic.main.activity_home_steam.*
import kotlinx.android.synthetic.main.activity_list_steam.*
import kotlinx.android.synthetic.main.activity_list_steam.animation_view_steam
import kotlinx.android.synthetic.main.activity_list_steam.rvSteam

class ListSteamActivity : BaseActivity() {

    var TAG_GET_STEAM = "getSteam"
    lateinit var adapter: AdapterSteam

    var listSteam = ArrayList<Steam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_steam)

        mUserPref = UserPreference(this)
        adapter = AdapterSteam(listSteam)
        rvSteam.setHasFixedSize(true)
        rvSteam.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvSteam.adapter = adapter

        icAdd.setOnClickListener {
            startActivity(Intent(this, AddSteamActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        updateUI()
        getDataMySteam()
    }

    fun updateUI(){
        if (mUserPref.getJenisUser().equals("steam")){
            icAdd.visibility = View.VISIBLE
        }
    }

    fun getDataMySteam(){
        steamRef.get().addOnSuccessListener { result ->
            listSteam.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var steam : Steam = document.toObject(Steam::class.java)
                steam.id_steam = document.id

                if (mUserPref.getJenisUser().equals("steam")){
                    if (steam.id_pemilik.equals(auth.currentUser?.uid)){
                        listSteam.add(steam)
                    }
                }else{
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

    override fun onResume() {
        super.onResume()
        getDataMySteam()
    }
}
