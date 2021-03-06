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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListBookingActivity : BaseActivity() {

    var TAG_GET = "getBooking"
    lateinit var adapter: AdapterBooking
    lateinit var i : Intent
    lateinit var steam : Steam

    var listBooking = ArrayList<Booking>()
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val currentDate = sdf.format(Date())

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
        /*val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())*/
        bookingRef.whereEqualTo("tanggal",currentDate.toString())
            .get().addOnSuccessListener { result ->
            listBooking.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var booking : Booking = document.toObject(Booking::class.java)
                booking.id_booking = document.id
                checkDifference(booking)
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

    fun checkDifference(booking : Booking){
        var dateBooking = currentDate+" "+booking.jam
        var dateNow = ""+sdfTime.format(Date())

        var dtNow = sdfTime.parse(dateNow)
        var dtBooking = sdfTime.parse(dateBooking)
        var cal = Calendar.getInstance()
        cal.time = dtBooking
        cal.add(Calendar.MINUTE,30)
        dtBooking = cal.time

        var millis2 = dtBooking.time - dtNow.time
        val jam = (millis2 / (1000 * 60 * 60)).toInt()
        val menit = (millis2 / (1000 * 60) % 60).toInt()
        Log.d("checkDiff"," perbedaaan jam:"+jam+ " & menit : "+menit)

        if (jam == 0 && menit < 1){
            Log.d("checkDiff","ada booking yang habis di pengguna : "+booking.nama_user)
            deleteBooking(booking)
        }
    }

    fun deleteBooking(booking: Booking){
        bookingRef.document(booking.id_booking).delete().addOnSuccessListener {
            Log.d("deletebooking","telah dihapus booking pengguna : "+booking.nama_user)
            Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
            getDataBooking()
        }.addOnFailureListener {
                e ->
            showErrorMessage("terjadi kesalahan "+e)
            Log.w("deleteDoc", "Error deleting document", e)
        }
    }
}
