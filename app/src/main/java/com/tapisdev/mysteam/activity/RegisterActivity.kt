package com.tapisdev.mysteam.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.MainActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {

    var TAG_SIMPAN = "simpanUser"
    lateinit var userModel : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mUserPref = UserPreference(this)

        backToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        kembali.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        cirRegisterButton.setOnClickListener {
            checkValidation()
        }
    }

    fun checkValidation(){
        var getName = editTextName.text.toString()
        var getEmail = editTextEmail.text.toString()
        var getPhone = editTextMobile.text.toString()
        var getPassword = editTextPassword.text.toString()

        if (getName.equals("") || getName.length == 0){
            showErrorMessage("Nama Belum diisi")
        }else  if (getEmail.equals("") || getEmail.length == 0){
            showErrorMessage("email Belum diisi")
        }
        else  if (getPhone.equals("") || getPhone.length == 0){
            showErrorMessage("phone Belum diisi")
        }else  if (getPassword.equals("") || getEmail.length == 0){
            showErrorMessage("password Belum diisi")
        }else if(getPassword.length < 8 ){
            showErrorMessage("Password harus lebih dari 8 karakter")
        }
        else{
            userModel = UserModel(getName,getEmail,"",getPhone,"pengguna","")
            Log.d(TAG_SIMPAN," user regis : "+userModel.toString())

            registerUser(userModel.email,getPassword)
        }
    }

    fun registerUser(email : String,pass : String){
        showLoading(this)
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(this, OnCompleteListener{task ->
            if (task.isSuccessful){
                var userId = auth.currentUser?.uid

                if (userId != null) {
                    userModel.uId = userId
                    userRef.document(userId).set(userModel).addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            dismissLoading()
                            showLongSuccessMessage("Pendaftaran Berhasil, Silakan Login Untuk Melanjutkan")

                            startActivity(Intent(this, MainActivity::class.java))
                            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)

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
}
