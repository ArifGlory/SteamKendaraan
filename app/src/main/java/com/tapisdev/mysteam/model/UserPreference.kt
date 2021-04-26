package com.tapisdev.mysteam.model

import android.content.Context

class UserPreference(context: Context) {
    private val KEY_NAME = "name"
    private val KEY_JENIS_USER = "jenis_user"
    private val KEY_EMAIL = "email"
    private val KEY_FOTO = "foto"
    private val KEY_PHONE = "phone"
    private val KEY_ALAMAT = "alamat"
    private val KEY_LATLON = "latlon"
    private val KEY_DESKRIPSI = "deskripsi"

    var PREFS_NAME = "UserPref"
    private val preferences  = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    fun saveName(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_NAME,text)
        editor.commit()
    }

    fun saveJenisUser(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_JENIS_USER,text)
        editor.commit()
    }

    fun saveEmail(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_EMAIL,text)
        editor.commit()
    }

    fun saveFoto(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_FOTO,text)
        editor.commit()
    }

    fun savePhone(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_PHONE,text)
        editor.commit()
    }

    fun saveAlamat(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_ALAMAT,text)
        editor.commit()
    }

    fun saveLatlon(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_LATLON,text)
        editor.commit()
    }

    fun saveDeskripsi(text : String){
        val editor = preferences!!.edit()
        editor.putString(KEY_DESKRIPSI,text)
        editor.commit()
    }

    fun getName() : String?{
        return preferences!!.getString(KEY_NAME,null)
    }

    fun getJenisUser() : String?{
        var jenisUser = preferences?.getString(KEY_JENIS_USER,"none")
        return jenisUser
    }

    fun getEmail() : String?{
        return preferences!!.getString(KEY_EMAIL,null)
    }

    fun getFoto() : String?{
        return preferences!!.getString(KEY_FOTO,null)
    }

    fun getPhone() : String?{
        return preferences!!.getString(KEY_PHONE,null)
    }

    fun getAlamat() : String?{
        return preferences!!.getString(KEY_ALAMAT,null)
    }

    fun getLatlon() : String?{
        return preferences!!.getString(KEY_LATLON,null)
    }

    fun getDeskripsi() : String?{
        return preferences!!.getString(KEY_DESKRIPSI,null)
    }



}