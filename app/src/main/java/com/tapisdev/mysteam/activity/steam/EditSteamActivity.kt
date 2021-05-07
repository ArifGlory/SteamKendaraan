package com.tapisdev.mysteam.activity.steam

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.activity.SplashActivity
import com.tapisdev.mysteam.model.SharedVariable
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import com.tapisdev.mysteam.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_steam.*
import kotlinx.android.synthetic.main.activity_edit_steam.*
import kotlinx.android.synthetic.main.activity_edit_steam.edAlamat
import kotlinx.android.synthetic.main.activity_edit_steam.edName
import kotlinx.android.synthetic.main.activity_edit_steam.spJenisKendaraan
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class EditSteamActivity : BaseActivity(),PermissionHelper.PermissionListener {

    lateinit var i : Intent
    lateinit var steam : Steam

    var TAG_EDIT = "ubahSteam"
    var TAG_SPINNEr = "spinKendaraan"
    var selectedKendaraan = "none"

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    var lat = 0.0
    var lon = 0.0

    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_steam)
        mUserPref = UserPreference(this)
        i = intent
        steam = i.getSerializableExtra("steam") as Steam
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        btnUpdate.setOnClickListener {
            checkValidation()
        }
        btnUbahLokasi.setOnClickListener {
            startActivity(Intent(this, SelectLokasiActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        ivGallery.setOnClickListener {
            launchGallery()
        }
        spJenisKendaraan.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                var pos = position+1
                //selectedKendaraan = parent?.get(position).toString()

                //Log.d(TAG_SPINNEr,"nama jenis : "+selectedKendaraan)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        updateUI()
    }

    fun checkValidation(){
        var getName = edName.text.toString()
        var getAlamat = edAlamat.text.toString()

        selectedKendaraan = spJenisKendaraan.selectedItem.toString()
        Log.d(TAG_SPINNEr,"nama jenis : "+selectedKendaraan)


        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getAlamat.equals("") || getAlamat.length == 0){
            showErrorMessage("Alamat Belum diisi")
        } else if (lat == 0.0){
            showErrorMessage("Lokasi belum dpilih")
        }else if (selectedKendaraan.equals("none") || selectedKendaraan.equals("Pilih Jenis Kendaraan")){
            showErrorMessage("Anda belum memilih jenis kendaraan")
        }
        else if (fileUri == null){
            updateDataOnly(getName,getAlamat)
        }
        else {
            uploadFoto(getName,getAlamat)
        }
    }

    fun updateDataOnly(name : String,alamat : String){
        showLoading(this)
        steamRef.document(steam.id_steam).update("nama_steam",name)
        steamRef.document(steam.id_steam).update("jenis_kendaraan",selectedKendaraan)
        steamRef.document(steam.id_steam).update("lat",lat.toString())
        steamRef.document(steam.id_steam).update("lon",lon.toString())
        steamRef.document(steam.id_steam).update("alamat",alamat).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah data berhasil")
                startActivity(Intent(this, ListSteamActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                finish()
            }else{
                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                Log.d(TAG_EDIT,"err : "+task.exception)
            }
        }
    }

    fun uploadFoto(name : String,alamat : String){
        showLoading(this)

        if (fileUri != null){
            Log.d(TAG_EDIT,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_EDIT, exception.toString())
            }.addOnSuccessListener {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                showSuccessMessage("Image Berhasil di upload")
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                    }

                    fileReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(mAu.getInstance().getCurrentUser().getUid());
                        val url = downloadUri!!.toString()
                        steamRef.document(steam.id_steam).update("nama_steam",name)
                        steamRef.document(steam.id_steam).update("jenis_kendaraan",selectedKendaraan)
                        steamRef.document(steam.id_steam).update("lat",lat.toString())
                        steamRef.document(steam.id_steam).update("lon",lon.toString())
                        steamRef.document(steam.id_steam).update("alamat",alamat)
                        Log.d(TAG_EDIT,"download URL : "+ downloadUri.toString())// This is the one you should store
                        steamRef.document(steam.id_steam).update("foto",url).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah data berhasil")
                                startActivity(Intent(this, ListSteamActivity::class.java))
                                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                                finish()
                            }else{
                                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                                Log.d(TAG_EDIT,"err : "+task.exception)
                            }
                        }
                    } else {
                        dismissLoading()
                        showErrorMessage("Terjadi kesalahan, coba lagi nanti")
                    }
                }
            }.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                pDialogLoading.setTitleText("Uploaded " + progress.toInt() + "%...")
            }


        }else{
            dismissLoading()
            showErrorMessage("Anda belum memilih file")
        }
    }

    fun updateUI(){
        edName.setText(steam.nama_steam)
        edAlamat.setText(steam.alamat)
        Glide.with(this)
            .load(steam.foto)
            .into(ivSteam)

        selectedKendaraan = steam.jenis_kendaraan
        if (steam.jenis_kendaraan.equals("Motor")){
            spJenisKendaraan.setSelection(1)
        }else if (steam.jenis_kendaraan.equals("Mobil")){
            spJenisKendaraan.setSelection(2)
        }else if (steam.jenis_kendaraan.equals("Semua")){
            spJenisKendaraan.setSelection(3)
        }else{
            spJenisKendaraan.setSelection(0)
        }

        lat = steam.lat.toDouble()
        lon = steam.lon.toDouble()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            fileUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                fotoBitmap = bitmap
                ivSteam.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGallery() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
    }

    override fun onPermissionCheckDone() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onResume() {
        super.onResume()
        if (SharedVariable.centerLatLon.latitude != 0.0){
            lat = SharedVariable.centerLatLon.latitude
            lon = SharedVariable.centerLatLon.longitude

            val img: Drawable = btnUbahLokasi.context.resources.getDrawable(R.drawable.ic_check_black_24dp)
            btnUbahLokasi.setText("Lokasi Telah dipilih")
            btnUbahLokasi.setCompoundDrawables(img,null,null,null)
        }
    }
}
