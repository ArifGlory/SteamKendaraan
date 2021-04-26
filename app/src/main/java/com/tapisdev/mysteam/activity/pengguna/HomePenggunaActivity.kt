package com.tapisdev.mysteam.activity.pengguna

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import kotlinx.android.synthetic.main.activity_home_pengguna.*

class HomePenggunaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_pengguna)

        lineProfil.setOnClickListener {
            startActivity(Intent(this, ProfilPenggunaActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }
}
