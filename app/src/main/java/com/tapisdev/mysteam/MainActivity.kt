package com.tapisdev.mysteam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.activity.RegisterActivity
import com.tapisdev.mysteam.activity.SplashActivity
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.sign

class MainActivity : BaseActivity() {

    var TAG_LOGIN = "login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mUserPref = UserPreference(this)

        cirLoginButton.setOnClickListener {
            checkValidation()
        }
        toDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        keDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    fun checkValidation(){
        var getEmail = editTextEmail.text.toString()
        var getPass  = editTextPassword.text.toString()

        if (getEmail.equals("") || getEmail.length == 0){
            showErrorMessage("Email harus diisi")
        }else if (getPass.equals("") || getPass.length == 0){
            showErrorMessage("Password harus diisi")
        }else{
            signIn(getEmail,getPass)
        }
    }

    fun signIn(email : String,pass : String){
        showLoading(this)
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, OnCompleteListener { task ->
            if(task.isSuccessful){
                var userId = auth.currentUser?.uid
                Log.d(TAG_LOGIN,"user ID : "+userId)


                startActivity(Intent(this, SplashActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay)


            }else{
                dismissLoading()
                showErrorMessage("Password / Email salah")
            }
        })
    }

    fun setSession(userModel: UserModel){
        mUserPref.saveName(userModel.name)
        mUserPref.saveEmail(userModel.email)
        mUserPref.saveFoto(userModel.foto)
        mUserPref.saveJenisUser(userModel.jenis)
        mUserPref.savePhone(userModel.phone)
    }
}
