package com.tapisdev.mysteam.activity.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import com.tapisdev.mysteam.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_add_pemilik_steam.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.ArrayList

class AddPemilikSteamActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var TAG_SIMPAN = "simpanUser"
    lateinit var userModel : UserModel
    lateinit var password : String

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pemilik_steam)
        mUserPref = UserPreference(this)

        storageReference = FirebaseStorage.getInstance().reference.child("images")

        permissionHelper = PermissionHelper(this)
        permissionHelper.setPermissionListener(this)

        ivProfile.setOnClickListener {
            launchGallery()
        }
        btnDaftarkan.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getName = editTextName.text.toString()
        var getEmail = editTextEmail.text.toString()
        var getPhone = editTextMobile.text.toString()
        var getPassword = editTextPassword.text.toString()
        password = getPassword

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        } else if (getEmail.equals("") || getEmail.length == 0){
            showErrorMessage("Email Belum diisi")
        } else if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("Telepon Belum diisi")
        }else if (getPassword.equals("") || getPassword.length == 0){
            showErrorMessage("Password Belum diisi")
        }else if (getPassword.length < 8){
            showErrorMessage("Password harus lebih dari 8 karakter")
        }else if (fileUri == null){
            showErrorMessage("anda belum memilih foto")
        }
        else {
            userModel = UserModel(getName,getEmail,"",getPhone,"steam","")
            uploadFoto()
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
                        userModel.foto = url
                        savePemilikSteam()
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

    fun savePemilikSteam(){
        pDialogLoading.setTitleText("menyimpan data..")
        showInfoMessage("Sedang menyimpan ke database..")

        auth.createUserWithEmailAndPassword(userModel.email,password).addOnCompleteListener(this, OnCompleteListener{task ->
            if (task.isSuccessful){
                var userId = auth.currentUser?.uid

                if (userId != null) {
                    userModel.uId = userId
                    userRef.document(userId).set(userModel).addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            dismissLoading()
                            showLongSuccessMessage("Pendaftaran Pemilik Steam Berhasil")
                            onBackPressed()
                        }else{
                            dismissLoading()
                            showLongErrorMessage("Error pendaftaran, coba lagi nanti ")
                            Log.d(TAG_SIMPAN,"err : "+task.exception)
                        }
                    }
                }else{
                    showLongErrorMessage("user id tidak di dapatkan")
                }
            }else{
                dismissLoading()
                if(task.exception?.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")!!){
                    showLongErrorMessage("Email sudah pernah digunakan ")
                }else{
                    showLongErrorMessage("Error pendaftaran, Cek apakah email sudah pernah digunakan / belum dan  coba lagi nanti ")
                    Log.d(TAG_SIMPAN,"err : "+task.exception)
                }

            }
        })

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
}
