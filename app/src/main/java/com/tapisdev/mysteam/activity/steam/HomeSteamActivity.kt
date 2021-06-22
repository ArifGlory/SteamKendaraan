package com.tapisdev.mysteam.activity.steam

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
import com.tapisdev.mysteam.adapter.AdapterSteam
import com.tapisdev.mysteam.model.Booking
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_home_steam.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeSteamActivity : BaseActivity() {

    var TAG_GET_STEAM = "getSteam"
    var TAG_GET = "getBooking"
    lateinit var adapter: AdapterSteam
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val currentDate = sdf.format(Date())
    var listBooking = ArrayList<Booking>()

    var listSteam = ArrayList<Steam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_steam)
        mUserPref = UserPreference(this)
        adapter = AdapterSteam(listSteam)
        rvSteam.setHasFixedSize(true)
        rvSteam.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvSteam.adapter = adapter

        lineAddSteam.setOnClickListener {
            startActivity(Intent(this, AddSteamActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineLogout.setOnClickListener {
            logout()
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineSteamSaya.setOnClickListener {
            startActivity(Intent(this, ListSteamActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        lineProfil.setOnClickListener {
            startActivity(Intent(this, ProfilPenggunaActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        getDataMySteam()
        getDataBooking()
    }

    fun getDataBooking(){
        /*val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())*/
        Log.d(TAG_GET,"tanggla "+currentDate.toString())
        pgNotif.visibility = View.VISIBLE
        bookingRef.whereEqualTo("tanggal",currentDate.toString())
            .get().addOnSuccessListener { result ->
                listBooking.clear()
                //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
                pgNotif.visibility = View.INVISIBLE
                for (document in result){
                    //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                    var booking : Booking = document.toObject(Booking::class.java)
                    booking.id_booking = document.id
                    //sa;ah disni
                    if (booking.id_pemilik.equals(auth.currentUser?.uid)){
                        listBooking.add(booking)
                    }
                    if(listBooking.size > 0){
                        showInfoMessage("Ada Booking Baru !")
                        tvNotif.visibility = View.VISIBLE
                        tvNotif.setText("Ada "+listBooking.size+" booking baru, silahkan cek ke Menu Steam anda")
                    }else{
                        tvNotif.visibility = View.VISIBLE
                        tvNotif.setText("Belum ada booking baru untuk saat ini")
                    }

                }

            }.addOnFailureListener { exception ->
                pgNotif.visibility = View.INVISIBLE
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET,"err : "+exception.message)
            }
    }

    fun getDataMySteam(){
        steamRef.limit(5).get().addOnSuccessListener { result ->
            listSteam.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var steam : Steam = document.toObject(Steam::class.java)
                steam.id_steam = document.id
                if (steam.id_pemilik.equals(auth.currentUser?.uid)){
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
