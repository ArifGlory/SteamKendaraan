package com.tapisdev.cateringtenda.base

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Info
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.tapisdev.mysteam.activity.steam.LokasiSteamActivity
import com.tapisdev.mysteam.model.GraphNode
import com.tapisdev.mysteam.model.SharedVariable
import com.tapisdev.mysteam.model.SharedVariable.Companion.uniqJalurID
import com.tapisdev.mysteam.model.UserPreference
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

open class BaseActivity : AppCompatActivity() {

    lateinit var pDialogLoading : SweetAlertDialog
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var currentUser : FirebaseUser
    lateinit var mUserPref : UserPreference

    val myDB = FirebaseFirestore.getInstance()
    val userRef = myDB.collection("users")
    val settingsRef = myDB.collection("settings")
    val steamRef = myDB.collection("steam")
    val fasilitasRef = myDB.collection("fasilitas")
    val ratingRef = myDB.collection("rating")
    val bookingRef = myDB.collection("booking")
    val graphNodeRef = myDB.collection("graph_node")


    override fun setContentView(view: View?) {
        super.setContentView(view)

        pDialogLoading = SweetAlertDialog(applicationContext, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        var settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        myDB.firestoreSettings = settings

    }

    open fun showLoading(mcontext : Context){
        pDialogLoading = SweetAlertDialog(mcontext, SweetAlertDialog.PROGRESS_TYPE)
        pDialogLoading.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialogLoading.setTitleText("Loading..")
        pDialogLoading.setCancelable(false)

        pDialogLoading.show()
    }

    fun dismissLoading(){
        pDialogLoading.dismiss()
    }

    fun showErrorMessage(message : String){
        applicationContext?.let { Toasty.error(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showSuccessMessage(message : String){
        applicationContext?.let { Toasty.success(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showLongSuccessMessage(message : String){
        applicationContext?.let { Toasty.success(it, message, Toast.LENGTH_LONG, true).show() }
    }

    fun showLongErrorMessage(message : String){
        applicationContext?.let { Toasty.error(it, message, Toast.LENGTH_LONG, true).show() }
    }

    fun showInfoMessage(message : String){
        applicationContext?.let { Toasty.info(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showWarningMessage(message : String){
        applicationContext?.let { Toasty.warning(it, message, Toast.LENGTH_SHORT, true).show() }
    }

    fun showSweetInfo(message : String){
        SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            .setTitleText("Informasi")
            .setContentText(message)
            .show()
    }

    fun logout(){
        mUserPref.saveName("")
        mUserPref.saveAlamat("")
        mUserPref.saveDeskripsi("")
        mUserPref.saveEmail("")
        mUserPref.saveJenisUser("")
        mUserPref.savePhone("")
    }

    fun convertDate(tanggal : String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val output = formatter.format(parser.parse(tanggal))

        return output
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            currentUser = auth.currentUser!!
        }
    }

    fun getGraphNode(koordinatAwal : LatLng,koordinatAkhir : LatLng){
        //mengambil node
        GoogleDirection.withServerKey("AIzaSyBYR_9WSimaGMFhwRyTSy25DKPrSnl97uc")
            .from(koordinatAwal)
            .to(koordinatAkhir)
            .transportMode(TransportMode.DRIVING)
            .execute(object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?) {
                    if (direction!!.isOK) {
                        val route =
                            direction.routeList[0]
                        val leg = route.legList[0]
                        val stepList =
                            leg.stepList
                        val nodeList: List<LatLng> = leg.directionPoint


                        //ubah menjadi JSONArray
                        val ja = JSONArray()
                        for (i in 0 until nodeList.size) {
                            val koordinat =
                                "" + nodeList[i].latitude + "," + nodeList[i].longitude
                            ja.put(koordinat)
                        }
                        val rootObject = JSONObject()
                        rootObject.put("koordinat_node",ja)

                        //simpan graph node nya
                        var bobotInfo =
                            Info()
                        bobotInfo = leg.distance
                        var bobot = bobotInfo.text
                        bobot = bobot.dropLast(2)
                        val graphNode = GraphNode(
                            rootObject.toString(),
                            uniqJalurID,
                            bobot
                        )
                        graphNodeRef.document(SharedVariable.uniqJalurID).set(graphNode).addOnCompleteListener(
                            OnCompleteListener<Void?> { task ->
                                if (task.isSuccessful) {
                                    SharedVariable.graphNodeStatus = true
                                } else {
                                    Log.d("djikstra", "Gagal mmenyimpan node ke DB")
                                    Toasty.error(
                                        applicationContext,
                                        "Tidak dapat menemukan rute",
                                        Toasty.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }

                override fun onDirectionFailure(t: Throwable) {
                    Log.d("djikstra", "Gagal mendapatkan graph ")
                    Toasty.error(applicationContext, "Tidak dapat menemukan rute", Toasty.LENGTH_SHORT)
                        .show()
                }
            })
    }


}