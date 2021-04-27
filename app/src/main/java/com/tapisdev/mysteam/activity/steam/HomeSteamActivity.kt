package com.tapisdev.mysteam.activity.steam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import kotlinx.android.synthetic.main.activity_home_steam.*

class HomeSteamActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_steam)

        lineAddSteam.setOnClickListener {

        }
    }
}
