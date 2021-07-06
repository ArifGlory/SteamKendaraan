package com.tapisdev.mysteam.activity.steam

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.SharedVariable
import com.tapisdev.mysteam.model.Steam
import com.tapisdev.mysteam.model.UserPreference
import com.tapisdev.mysteam.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_steam.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class AddSteamActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var TAG_SIMPAN = "simpanSteam"
    var TAG_SPINNEr = "spinKendaraan"
    var selectedKendaraan = "none"
    lateinit var steam : Steam

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
        setContentView(R.layout.activity_add_steam)
        mUserPref = UserPreference(this)

        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        ivProfile.setOnClickListener {
            launchGallery()
        }
        btnLokasi.setOnClickListener {
            startActivity(Intent(this, SelectLokasiActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        btnDaftarkan.setOnClickListener {
            checkValidation()
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
        }else if (fileUri == null){
            showErrorMessage("anda belum memilih foto")
        }else if (selectedKendaraan.equals("none") || selectedKendaraan.equals("Pilih Jenis Kendaraan")){
            showErrorMessage("Anda belum memilih jenis kendaraan")
        }
        else {
            steam = Steam(getName,
                getAlamat,
                "",
                lat.toString(),
                lon.toString(),
                selectedKendaraan,
                auth.currentUser!!.uid,
                "",
                "open")
            uploadFoto()
        }
    }

    fun saveSteam(){
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")

        steamRef.document().set(steam).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                var resetLokasi = LatLng(0.0,0.0)
                SharedVariable.centerLatLon = resetLokasi

                dismissLoading()
                showLongSuccessMessage("Tambah Steam Berhasil")
                onBackPressed()
            }else{
                dismissLoading()
                showLongErrorMessage("Error pendaftaran, coba lagi nanti ")
                Log.d(TAG_SIMPAN,"err : "+task.exception)
            }
        }
    }

    fun uploadFoto(){
        showLoading(this)

        if (fileUri != null){
            Log.d(TAG_SIMPAN,"uri :"+fileUri.toString())

            val baos = ByteArrayOutputStream()
            fotoBitmap?.compress(Bitmap.CompressFormat.JPEG,50,baos)
            val data: ByteArray = baos.toByteArray()

            val fileReference = storageReference!!.child(System.currentTimeMillis().toString())
            val uploadTask = fileReference.putBytes(data)

            uploadTask.addOnFailureListener {
                    exception -> Log.d(TAG_SIMPAN, exception.toString())
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
                        Log.d(TAG_SIMPAN,"download URL : "+ downloadUri.toString())// This is the one you should store
                        steam.foto = url
                        saveSteam()
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

    private fun launchGallery() {
        var listPermissions: MutableList<String> = ArrayList()
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionHelper.checkAndRequestPermissions(listPermissions)
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
                ivProfile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
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

            val img: Drawable = btnLokasi.context.resources.getDrawable(R.drawable.ic_check_black_24dp)
            btnLokasi.setText("Lokasi Telah dipilih")
            btnLokasi.setCompoundDrawables(img,null,null,null)

            var alamatLokasi = getCompleteAddress(lat,lon)
            edAlamat.setText(alamatLokasi)
        }
    }

    fun getCompleteAddress(latitude : Double,longitude : Double) : String{
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address: String = addresses[0]
            .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city: String = addresses[0].getLocality()
        val state: String = addresses[0].getAdminArea()
        val country: String = addresses[0].getCountryName()
        val postalCode: String = addresses[0].getPostalCode()
        val knownName: String =
            addresses[0].getFeatureName() // Only if available else return NULL

        return address
    }
}
