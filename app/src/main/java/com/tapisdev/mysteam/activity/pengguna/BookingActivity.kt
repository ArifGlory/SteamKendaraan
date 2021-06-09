package com.tapisdev.mysteam.activity.pengguna

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.firestore.DocumentSnapshot
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.Booking
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_booking.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class BookingActivity : BaseActivity() {

    lateinit var i : Intent
    lateinit var steam : Steam
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfTime = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val currentDate = sdf.format(Date())
    var TAG_GET_BOOKING = "getBooking"
    var TAG_SIMPAN = "saveBooking"
    var id_steam = ""
    var isBooking = false
    lateinit var myBooking : Booking

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        mUserPref = UserPreference(this)

        i = intent
       // steam = i.getSerializableExtra("steam") as Steam
        id_steam = i.getStringExtra("id_steam").toString()
        Log.d("id_steam",id_steam)
        timePicker.setIs24HourView(true)

        icBack.setOnClickListener {
            onBackPressed()
        }
        btnBook.setOnClickListener {
            if (isBooking){
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Yakin Hapus Booking Aktif ?")
                    .setContentText("hapus booking tidak bisa dikembalikan")
                    .setConfirmText("Ya")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            deleteBooking()
                        }
                    }
                    .setCancelButton(
                        "Tidak"
                    ) { sDialog -> sDialog.dismissWithAnimation() }
                    .show()
            }else{
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

        }

        getDetailSteam()
        cekMyBooking()
    }

    fun getDetailSteam(){
        //showLoading(this)
        steamRef.document(id_steam).get().addOnSuccessListener {
            document ->
           // dismissLoading()
            if (document != null){
                steam = document.toObject(Steam::class.java)!!
                steam.id_steam = id_steam
                Log.d("detailSteam",steam.toString())
            }else{
                showErrorMessage("Steam tidak ditemukan")
                onBackPressed()
            }

        }.addOnFailureListener {
            //dismissLoading()
            showErrorMessage("terjadi kesalahan coba lagi nanti")
            Log.d("detailSteam",it.toString())
        }
    }

    fun deleteBooking(){
        showLoading(this)
        bookingRef.document(myBooking.id_booking).delete().addOnSuccessListener {
            dismissLoading()
            showSuccessMessage("Booking Aktif Berhasil Dihapus")
            isBooking = false
            updateUI()

            Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
        }.addOnFailureListener {
                e ->
            dismissLoading()
            showErrorMessage("terjadi kesalahan "+e)
            Log.w("deleteDoc", "Error deleting document", e)
        }
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
            "waiting",
            auth.currentUser!!.uid!!,
            steam.id_pemilik,
            id_steam,
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
            tvStatusBookingUser.setText("Anda memiliki booking hari ini pada jam "+myBooking.jam+" \n di "+myBooking.nama_steam+". \n Pastikan datang tepat waktu, jika melebihi 30menit dari waktu booking, maka booking steam anda akan hangus.")
            btnBook.setText("Hapus Booking Aktif")
            timePicker.visibility = View.INVISIBLE
            checkDifferenceTime()

        }else{
            tvStatusBookingUser.setText("Anda belum memiliki booking hari ini, silahkan melakukan proses Booking")
            btnBook.setText("Booking")
            timePicker.visibility = View.VISIBLE
        }

    }

    fun checkDifferenceTime(){
        var dateBooking = currentDate+" "+myBooking.jam
        var dateNow = ""+sdfTime.format(Date())

        var dtNow = sdfTime.parse(dateNow)
        var dtBooking = sdfTime.parse(dateBooking)


        val timeFormat: DateFormat = SimpleDateFormat("HH:mm")
        var cal = Calendar.getInstance()
        cal.time = dtBooking
        cal.add(Calendar.MINUTE,30)
        dtBooking = cal.time

        var millis2 = dtBooking.time - dtNow.time
        val jam = (millis2 / (1000 * 60 * 60)).toInt()
        val menit = (millis2 / (1000 * 60) % 60).toInt()
        Log.d("checkDiff"," perbedaaan jam:"+jam+ " & menit : "+menit)

        if (jam == 0 && menit < 1){
            showInfoMessage("Masa Booking telah habis, booking anda akan dihapus secara otomatis")
            deleteBooking()
        }
    }
}
