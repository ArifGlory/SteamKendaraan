package com.tapisdev.mysteam.activity.pengguna

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.Query
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.Booking
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_booking.*
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var steam : Steam
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = sdf.format(Date())
    var TAG_GET_BOOKING = "getBooking"
    var TAG_SIMPAN = "saveBooking"
    var isBooking = false
    lateinit var myBooking : Booking

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        mUserPref = UserPreference(this)

        i = intent
        steam = i.getSerializableExtra("steam") as Steam
        timePicker.setIs24HourView(true)

        icBack.setOnClickListener {
            onBackPressed()
        }
        btnBook.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Yakin waktu sudah sesuai ?")
                .setContentText("Pastikan kembali waktu steam anda")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    saveBooking()
                }
                .setCancelButton(
                    "Tidak"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
        }

        cekMyBooking()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun saveBooking(){
        showLoading(this)
        var jam = ""+timePicker.hour+":"+timePicker.minute

        var booking = Booking(
            currentDate.toString(),
            jam,
            mUserPref.getName()!!,
            mUserPref.getFoto()!!,
            steam.nama_steam,
            auth.currentUser!!.uid!!,
            steam.id_pemilik,
            steam.id_steam,
            ""
        )

        bookingRef.document().set(booking).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                dismissLoading()
                showLongSuccessMessage("Booking berhasil dikirim")
                onBackPressed()
            }else{
                dismissLoading()
                showLongErrorMessage("Error, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }

    fun cekMyBooking(){
        showLoading(this)
        bookingRef.whereEqualTo("tanggal",currentDate.toString())
            .get().addOnSuccessListener { result ->
                dismissLoading()

                if (result.isEmpty){
                    isBooking = false
                    updateUI()
                    tvStatusBookingUser.setText("Anda belum memiliki booking hari ini, silahkan melakukan proses Booking")
                }else{
                    for (document in result){
                        //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                        var booking : Booking = document.toObject(Booking::class.java)
                        booking.id_booking = document.id

                        if (booking.id_user.equals(auth.currentUser?.uid)){
                            isBooking = true
                            myBooking = booking
                            updateUI()
                        }
                    }
                }

            }.addOnFailureListener { exception ->
                dismissLoading()
                showErrorMessage("terjadi kesalahan : "+exception.message)
                Log.d(TAG_GET_BOOKING,"err : "+exception.message)
            }
    }

    fun updateUI(){
        if (isBooking){
            tvStatusBookingUser.setText("Anda memiliki booking hari ini pada jam "+myBooking.jam+" \n di "+myBooking.nama_steam)
            btnBook.setText("Hapus Booking Aktif")
            timePicker.visibility = View.INVISIBLE
        }else{
            tvStatusBookingUser.setText("Anda belum memiliki booking hari ini, silahkan melakukan proses Booking")
            btnBook.setText("Booking")
            timePicker.visibility = View.VISIBLE
        }

    }
}
