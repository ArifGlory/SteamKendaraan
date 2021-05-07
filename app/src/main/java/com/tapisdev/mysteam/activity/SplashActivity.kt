package com.tapisdev.mysteam.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.MainActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.activity.admin.HomeAdminActivity
import com.tapisdev.mysteam.activity.pengguna.HomePenggunaActivity
import com.tapisdev.mysteam.activity.steam.HomeSteamActivity
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference

class SplashActivity : BaseActivity() {

    var TAG_dETAIL = "detail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mUserPref = UserPreference(this)
        if (auth.currentUser != null){

            settingsRef.document("maintenance").get().addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    var mode = task.result?.get("mode")
                    if (mode != null) {
                        if (mode.equals("1")){
                            auth.signOut()
                            val i = Intent(applicationContext,MaintenanceActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                    }
                }
            }

            userRef.document(auth.currentUser!!.uid).get().addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val document = task.result
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG_dETAIL, "DocumentSnapshot data: " + document.data)
                            //convert doc to object
                            var userModel : UserModel = document.toObject(UserModel::class.java)!!
                            Log.d(TAG_dETAIL,"usermodel name : "+userModel.name)
                            setSession(userModel)

                            Log.d("userpref"," jenis user : "+mUserPref.getJenisUser())
                            if (mUserPref.getJenisUser() != null){
                                if (mUserPref.getJenisUser().equals("admin")){
                                    val i = Intent(applicationContext,HomeAdminActivity::class.java)
                                    startActivity(i)
                                }else if(mUserPref.getJenisUser().equals("pengguna")){
                                    val i = Intent(applicationContext,HomePenggunaActivity::class.java)
                                    startActivity(i)
                                }else if(mUserPref.getJenisUser().equals("steam")){
                                    val i = Intent(applicationContext,HomeSteamActivity::class.java)
                                    startActivity(i)
                                }else{
                                    val i = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(i)
                                }
                            }else{
                                val i = Intent(applicationContext, MainActivity::class.java)
                                startActivity(i)
                            }
                        } else {
                            Log.d(TAG_dETAIL, "No such document")
                            auth.signOut()
                            logout()
                            val i = Intent(applicationContext, MainActivity::class.java)
                            startActivity(i)
                        }
                    }
                }else{
                    showErrorMessage("Error saaat mencari di database")
                    Log.d(TAG_dETAIL,"err : "+task.exception)
                    auth.signOut()
                    logout()
                    val i = Intent(applicationContext, MainActivity::class.java)
                    startActivity(i)
                }
            }


        }else{
            val i = Intent(applicationContext,MainActivity::class.java)
            startActivity(i)
        }

    }

    fun setSession(userModel: UserModel){
        mUserPref.saveName(userModel.name)
        mUserPref.saveEmail(userModel.email)
        mUserPref.saveFoto(userModel.foto)
        mUserPref.saveJenisUser(userModel.jenis)
        mUserPref.savePhone(userModel.phone)
    }
}
