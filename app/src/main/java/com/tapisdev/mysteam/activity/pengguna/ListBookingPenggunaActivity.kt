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
import com.tapisdev.mysteam.adapter.AdapterBooking
import com.tapisdev.mysteam.adapter.AdapterBookingPengguna
import com.tapisdev.mysteam.model.Booking
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_list_booking_pengguna.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListBookingPenggunaActivity : BaseActivity() {

    var TAG_GET = "getBooking"
    lateinit var adapter: AdapterBookingPengguna
    lateinit var i : Intent
    lateinit var steam : Steam

    var listBooking = ArrayList<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_booking_pengguna)
        mUserPref = UserPreference(this)

        adapter = AdapterBookingPengguna(listBooking)
        rvBooking.setHasFixedSize(true)
        rvBooking.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvBooking.adapter = adapter

        getDataBooking()
    }

    fun getDataBooking(){
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        bookingRef.whereEqualTo("tanggal",currentDate.toString())
            .get().addOnSuccessListener { result ->
            listBooking.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var booking : Booking = document.toObject(Booking::class.java)
                booking.id_booking = document.id
                if (booking.id_user.equals(auth.currentUser?.uid)){
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
