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
import com.tapisdev.mysteam.model.UserPreference

class SplashActivity : BaseActivity() {

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
        }else{
            val i = Intent(applicationContext,MainActivity::class.java)
            startActivity(i)
        }

    }
}
