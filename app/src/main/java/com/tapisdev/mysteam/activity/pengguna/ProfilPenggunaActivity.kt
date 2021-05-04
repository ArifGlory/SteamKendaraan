package com.tapisdev.mysteam.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.MainActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_profil_pengguna.*

class ProfilPenggunaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_pengguna)
        mUserPref = UserPreference(this)

        btnLogout.setOnClickListener {
            logout()
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        updateUI()
    }

    fun updateUI(){
        edUserName.setText(mUserPref.getName())
        edMobileNumber.setText(mUserPref.getPhone())

        edUserName.isEnabled = false
        edMobileNumber.isEnabled = false
    }
}
