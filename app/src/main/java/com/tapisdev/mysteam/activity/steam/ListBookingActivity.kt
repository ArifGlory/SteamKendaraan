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
import com.tapisdev.mysteam.adapter.AdapterBooking
import com.tapisdev.mysteam.model.Booking
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_list_booking.*

class ListBookingActivity : BaseActivity() {

    var TAG_GET = "getBooking"
    lateinit var adapter: AdapterBooking
    lateinit var i : Intent
    lateinit var steam : Steam

    var listBooking = ArrayList<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_booking)
        mUserPref = UserPreference(this)
        i = intent
        steam = i.getSerializableExtra("steam") as Steam

        adapter = AdapterBooking(listBooking)
        rvBooking.setHasFixedSize(true)
        rvBooking.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvBooking.adapter = adapter

        getDataBooking()
    }

    fun getDataBooking(){
        bookingRef.get().addOnSuccessListener { result ->
            listBooking.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var booking : Booking = document.toObject(Booking::class.java)
                booking.id_booking = document.id
                if (booking.id_steam.equals(steam.id_steam)){
                    listBooking.add(booking)
                }
            }
            if (listBooking.size == 0){
                animation_view_booking.setAnimation(R.raw.empty_box)
                animation_view_booking.playAnimation()
                animation_view_booking.loop(false)
            }else{
                animation_view_booking.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET,"err : "+exception.message)
        }
    }
}
