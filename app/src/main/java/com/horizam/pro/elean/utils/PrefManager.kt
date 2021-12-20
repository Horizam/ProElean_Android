package com.horizam.pro.elean.utils

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.GsonBuilder
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.model.MyLocation

class PrefManager(_context: Context) {

    var pref: SharedPreferences = _context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    var accessToken:String
    get() = pref.getString(Constants.ACCESS_TOKEN, "")!!
    set(value) = pref.edit().putString(Constants.ACCESS_TOKEN, value).apply()

    var fcmToken:String
    get() = pref.getString(Constants.FCM_TOKEN, "")!!
    set(value) = pref.edit().putString(Constants.FCM_TOKEN, value).apply()

    var userId:Int
    get() = pref.getInt(Constants.USER_ID, 0)
    set(value) = pref.edit().putInt(Constants.USER_ID, value).apply()

    var isFreelancer:Int
    get() = pref.getInt(Constants.IS_FREELANCER, 0)
    set(value) = pref.edit().putInt(Constants.IS_FREELANCER, value).apply()

    var username: String?
    get() = pref.getString(Constants.USER_NAME, "")
    set(value) = pref.edit().putString(Constants.USER_NAME, value).apply()

    var location:MyLocation?
    get() {
       val value = pref.getString(Constants.LOCATION_KEY, null)
        return GsonBuilder().create().fromJson(value, MyLocation::class.java)
    }
    set(value) {
        val jsonString = GsonBuilder().create().toJson(value)
        pref.edit().putString(Constants.LOCATION_KEY, jsonString).apply()
    }

    fun clearAll(){
        pref.edit().clear().apply()
    }
}