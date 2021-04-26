package com.tapisdev.mysteam.activity.pengguna

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_profil_pengguna.*

class ProfilPenggunaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_pengguna)
        mUserPref = UserPreference(this)

        updateUI()
    }

    fun updateUI(){
        edUserName.setText(mUserPref.getName())
        edMobileNumber.setText(mUserPref.getPhone())

        edUserName.isEnabled = false
        edMobileNumber.isEnabled = false
    }
}
