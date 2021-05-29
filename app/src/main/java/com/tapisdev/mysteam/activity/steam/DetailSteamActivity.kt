package com.tapisdev.mysteam.activity.steam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.stepstone.apprating.AppRatingDialog
import com.stepstone.apprating.listener.RatingDialogListener
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.adapter.AdapterFasilitas
import com.tapisdev.mysteam.model.Fasilitas
import com.tapisdev.mysteam.model.Rating
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_detail_steam.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class DetailSteamActivity : BaseActivity(),RatingDialogListener {

    lateinit var i : Intent
    lateinit var steam : Steam

    var TAG_GET_Fasilitas = "getFasilitas"
    var TAG_RATING = "rating"
    lateinit var adapter: AdapterFasilitas

    var listFasilitas = ArrayList<Fasilitas>()
    var listRating = ArrayList<Rating>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_steam)
        mUserPref = UserPreference(this)
        i = intent

        steam = i.getSerializableExtra("steam") as Steam

        adapter = AdapterFasilitas(listFasilitas)
        rvFasilitas.setHasFixedSize(true)
        rvFasilitas.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvFasilitas.adapter = adapter


        ivBack.setOnClickListener {
            onBackPressed()
        }
        cvCekLokasi.setOnClickListener {
            val i = Intent(this,LokasiSteamActivity::class.java)
            i.putExtra("steam",steam as Serializable)
            startActivity(i)
        }
        cvUbah.setOnClickListener {
            val i = Intent(this,EditSteamActivity::class.java)
            i.putExtra("steam",steam as Serializable)
            startActivity(i)
        }
        cvHapus.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Anda yakin menghapus ini ?")
                .setContentText("Data yang sudah dihapus tidak bisa dikembalikan")
                .setConfirmText("Ya")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    showLoading(this)
                    steamRef.document(steam.id_steam).delete().addOnSuccessListener {
                        dismissLoading()
                        showSuccessMessage("Data berhasil dihapus")
                        onBackPressed()
                        Log.d("deleteDoc", "DocumentSnapshot successfully deleted!")
                    }.addOnFailureListener {
                            e ->
                        dismissLoading()
                        showErrorMessage("terjadi kesalahan "+e)
                        Log.w("deleteDoc", "Error deleting document", e)
                    }

                }
                .setCancelButton(
                    "Tidak"
                ) { sDialog -> sDialog.dismissWithAnimation() }
                .show()
        }
        ivAddFasilitas.setOnClickListener {
            val i = Intent(this,AddFasilitasActivity::class.java)
            i.putExtra("steam",steam as Serializable)
            startActivity(i)
        }
        cvRating.setOnClickListener {
            if (mUserPref.getJenisUser().equals("pengguna")){
                checkAlreadyRate()
            }else{
                showErrorMessage("Rating hanya dapat dilakukan oleh pengguna")
            }

        }

        updateUI()
        getDataFasilitas()
        getDataRating()
    }

    fun checkAlreadyRate(){
        showLoading(this)
        listRating.clear()
        ratingRef.get().addOnSuccessListener { result ->
            listRating.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var rating : Rating = document.toObject(Rating::class.java)
                rating.id_rating = document.id

                listRating.add(rating)
            }

            dismissLoading()
            if(listRating.size == 0){
                showDialog()
                Log.d(TAG_RATING,"here size 0")
            }else{
                for (i in 0 until listRating.size){
                    var ratings = listRating.get(i)
                    if (ratings.id_steam.equals(steam.id_steam) && ratings.id_user.equals(auth.currentUser!!.uid) ){
                        showInfoMessage("Anda sudah pernah mengirim rating ke steam ini")
                        break
                    }else{
                        showDialog()
                    }
                }
            }



        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan dalam rating : "+exception.message)
            Log.d(TAG_RATING,"err : "+exception.message)
        }
    }

    fun updateUI(){
        if (mUserPref.getJenisUser().equals("admin") || steam.id_pemilik.equals(auth.currentUser?.uid)){
            lineSetting.visibility = View.VISIBLE
            ivAddFasilitas.visibility = View.VISIBLE
        }
        if (mUserPref.getJenisUser().equals("pengguna")){
            cvRating.visibility = View.VISIBLE
        }else{
            cvRating.visibility = View.GONE
        }

        tvNamaSteam.setText(steam.nama_steam)
        tvAlamat.setText(steam.alamat)
        tvJenisKendaraan.setText(steam.jenis_kendaraan)
        Glide.with(this)
            .load(steam.foto)
            .into(ivSteam)
    }

    fun getDataFasilitas(){
        fasilitasRef.get().addOnSuccessListener { result ->
            listFasilitas.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var fasilitas : Fasilitas = document.toObject(Fasilitas::class.java)
                fasilitas.id_fasilitas = document.id
                if (fasilitas.id_steam.equals(steam.id_steam)){
                    listFasilitas.add(fasilitas)
                }
            }

            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_Fasilitas,"err : "+exception.message)
        }
    }

    fun getDataRating(){
        showLoading(this)
        var totalRate = 0

        ratingRef.get().addOnSuccessListener { result ->
            listRating.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var rating : Rating = document.toObject(Rating::class.java)
                rating.id_rating = document.id

                listRating.add(rating)
            }

            dismissLoading()
            if(listRating.size == 0){
                tvRatingTotal.text = " - "
            }else{
                for (i in 0 until listRating.size){
                    var ratings = listRating.get(i)
                    if (ratings.id_steam.equals(steam.id_steam)){
                        totalRate = totalRate + ratings.nilai_rating
                    }
                }
                var avg = 0.0
                avg = (totalRate / listRating.size).toDouble()
                tvRatingTotal.text = ""+avg
            }



        }.addOnFailureListener { exception ->
            dismissLoading()
            showErrorMessage("terjadi kesalahan dalam rating : "+exception.message)
            Log.d(TAG_RATING,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataFasilitas()
    }

    fun refreshList(){
        getDataFasilitas()
    }

    private fun showDialog() {
        AppRatingDialog.Builder()
            .setPositiveButtonText("Kirim")
            .setNegativeButtonText("Cancel")
            .setNeutralButtonText("nanti")
            .setNoteDescriptions(
                Arrays.asList(
                    "Sangat Buruk",
                    "Buruk",
                    "Biasa saja",
                    "Bagus",
                    "Sempurna !!!"
                )
            )
            .setDefaultRating(2)
            .setTitle("Rating Pelayanan")
            .setDescription("Beri Rating pada Rute Steam ini")
            .setCommentInputEnabled(false)
            .setDefaultComment("Thanks")
            .setStarColor(R.color.orange_500)
            .setNoteDescriptionTextColor(R.color.black)
            .setTitleTextColor(R.color.colorPrimary)
            .setDescriptionTextColor(R.color.grey_800)
            .setHint("Tuliskan Komentarmu disini ...")
            .setHintTextColor(R.color.grey_800)
            .setCommentTextColor(R.color.grey_800)
            .setCommentBackgroundColor(R.color.colorPrimaryDark)
            .setWindowAnimation(R.style.MyDialogFadeAnimation)
            .setCancelable(false)
            .setCanceledOnTouchOutside(false)
            .create(this)
            .show()
    }


    override fun onNegativeButtonClicked() {

    }

    override fun onNeutralButtonClicked() {

    }

    override fun onPositiveButtonClicked(rate: Int, comment: String) {

        var rating = Rating(rate,comment,auth.currentUser!!.uid,steam.id_pemilik,steam.id_steam,"")
        sendRating(rating)
    }

    fun sendRating(ratingInfo : Rating){
        showLoading(this)
        pDialogLoading.setTitleText("menyimpan data..")

        ratingRef.document().set(ratingInfo).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                dismissLoading()
                showLongSuccessMessage("Tambah Rating Berhasil")
                onBackPressed()
            }else{
                dismissLoading()
                showLongErrorMessage("Error, coba lagi nanti ")
                Log.d(TAG_RATING,"err : "+task.exception)
            }
        }
    }
}
