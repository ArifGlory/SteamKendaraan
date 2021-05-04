package com.tapisdev.mysteam.activity.pengguna

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.MainActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.activity.SplashActivity
import com.tapisdev.mysteam.activity.steam.AddSteamActivity
import com.tapisdev.mysteam.model.UserPreference
import com.tapisdev.mysteam.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_steam.*
import kotlinx.android.synthetic.main.activity_profil_pengguna.*
import kotlinx.android.synthetic.main.activity_profil_pengguna.ivProfile
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class ProfilPenggunaActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var editMode = 0
    var TAG_EDIT = "editProfil"
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_pengguna)
        mUserPref = UserPreference(this)
        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        btnLogout.setOnClickListener {
            logout()
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        btnEdit.setOnClickListener {
            if (editMode == 0){
                editMode = 1
                updateUI()
            }else{
                checkValidation()
            }

        }
        ivGallery.setOnClickListener {
            launchGallery()
        }

        updateUI()

    }

    fun checkValidation(){
        var getName = edName.text.toString()
        var getPhone = edMobileNumber.text.toString()


        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("telepon Belum diisi")
        } else if (fileUri == null){
            updateDataOnly(getName,getPhone)
        }
        else {
            uploadFoto(getName,getPhone)
        }
    }

    fun updateDataOnly(name : String,phone : String){
        showLoading(this)
        userRef.document(auth.currentUser!!.uid).update("name",name)
        userRef.document(auth.currentUser!!.uid).update("phone",phone).addOnCompleteListener { task ->
            dismissLoading()
            if (task.isSuccessful){
                showSuccessMessage("Ubah data berhasil")
                startActivity(Intent(this, SplashActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
            }else{
                showLongErrorMessage("terjadi kesalahan : "+task.exception)
                Log.d(TAG_EDIT,"err : "+task.exception)
            }
        }
    }

    fun uploadFoto(name : String,phone : String){
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
                        userRef.document(auth.currentUser!!.uid).update("name",name)
                        userRef.document(auth.currentUser!!.uid).update("phone",phone)
                        Log.d(TAG_EDIT,"download URL : "+ downloadUri.toString())// This is the one you should store
                        userRef.document(auth.currentUser!!.uid).update("foto",url).addOnCompleteListener { task ->
                            dismissLoading()
                            if (task.isSuccessful){
                                showSuccessMessage("Ubah data berhasil")
                                startActivity(Intent(this, SplashActivity::class.java))
                                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
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
        if (editMode == 0){
            edUserName.setText(mUserPref.getName())
            edMobileNumber.setText(mUserPref.getPhone())
            Glide.with(this)
                .load(mUserPref.getFoto())
                .into(ivProfile)

            edUserName.isEnabled = false
            edMobileNumber.isEnabled = false
            ivGallery.isEnabled = false
        }else{
            edUserName.isEnabled = true
            edMobileNumber.isEnabled = true
            ivGallery.isEnabled = true

            edUserName.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_corner_white_10))
            edMobileNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_corner_white_10))

            btnEdit.setText("Simpan Perubahan")
        }

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
}
