package com.tapisdev.mysteam.activity.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapisdev.cateringtenda.base.BaseActivity
import com.tapisdev.mysteam.R
import com.tapisdev.mysteam.adapter.AdapterPemilikSteam
import com.tapisdev.mysteam.model.UserModel
import com.tapisdev.mysteam.model.UserPreference
import kotlinx.android.synthetic.main.activity_list_pemilik.*

class ListPemilikActivity : BaseActivity() {

    var TAG_GET_Sparepart = "getSparepart"
    lateinit var adapter: AdapterPemilikSteam

    var listPemilik = ArrayList<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_pemilik)
        mUserPref = UserPreference(this)

        adapter = AdapterPemilikSteam(listPemilik)
        rvPemilik.setHasFixedSize(true)
        rvPemilik.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvPemilik.adapter = adapter

        getDataPemilikSteam()
    }

    fun getDataPemilikSteam(){
        userRef.get().addOnSuccessListener { result ->
            listPemilik.clear()
            //Log.d(TAG_GET_Sparepart," datanya "+result.documents)
            for (document in result){
                //Log.d(TAG_GET_Sparepart, "Datanya : "+document.data)
                var userModel : UserModel = document.toObject(UserModel::class.java)
                userModel.uId = document.id
                if (userModel.jenis.equals("steam")){
                    listPemilik.add(userModel)
                }
            }
            if (listPemilik.size == 0){
                animation_view_pemilik.setAnimation(R.raw.empty_box)
                animation_view_pemilik.playAnimation()
                animation_view_pemilik.loop(false)
            }else{
                animation_view_pemilik.visibility = View.INVISIBLE
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            showErrorMessage("terjadi kesalahan : "+exception.message)
            Log.d(TAG_GET_Sparepart,"err : "+exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getDataPemilikSteam()
    }
}
