package com.horizam.pro.elean.ui.main.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.horizam.pro.elean.R
import com.horizam.pro.elean.utils.PrefManager

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        loadInitialActivity()
    }

    private fun loadInitialActivity() {
        val prefManager = PrefManager(this)
        val intent: Intent = if (prefManager.accessToken.isEmpty()) {
            Intent(this@SplashActivity, AuthenticationActivity::class.java)
        } else {
            Intent(this@SplashActivity, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}