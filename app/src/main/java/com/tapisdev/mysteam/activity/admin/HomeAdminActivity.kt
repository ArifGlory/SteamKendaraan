package com.tapisdev.mysteam.activity.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import kotlinx.android.synthetic.main.activity_home_admin.*

class HomeAdminActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        lineAddPemilik.setOnClickListener {
            startActivity(Intent(this, AddPemilikSteamActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
        linePemilik.setOnClickListener {

        }
    }
}
