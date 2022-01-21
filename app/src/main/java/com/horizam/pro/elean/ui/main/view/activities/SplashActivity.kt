package com.horizam.pro.elean.ui.main.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.BaseUtils.Companion.screenHeight
import com.horizam.pro.elean.utils.BaseUtils.Companion.screenWidth
import com.horizam.pro.elean.utils.PrefManager

class SplashActivity : AppCompatActivity() {
    private val bundle = Bundle()
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        if (intent!!.hasExtra(Constants.TYPE)) {
            if (intent.getStringExtra(Constants.TYPE).toString() == Constants.MESSAGE) {
                bundle.putString(
                    Constants.TYPE,
                    intent.getStringExtra(Constants.TYPE).toString()
                )
                bundle.putString(
                    Constants.MESSAGE,
                    intent.getStringExtra(Constants.MESSAGE).toString()
                )
                bundle.putString(
                    Constants.SENDER_ID,
                    intent.getStringExtra(Constants.SENDER_ID).toString()
                )
                loadInitialActivityWithBundle()
            }else{
                loadInitialActivity()
                getScreenWidthAndHeight()
            }
        } else {
            loadInitialActivity()
            getScreenWidthAndHeight()
        }
    }

    private fun loadInitialActivityWithBundle() {
        val prefManager = PrefManager(this)
        val intent: Intent = if (prefManager.accessToken.isEmpty()) {
            Intent(this@SplashActivity, AuthenticationActivity::class.java)
        } else {
            Intent(this@SplashActivity, HomeActivity::class.java).apply {
                putExtras(bundle)
            }
        }
        startActivity(intent)
        finish()
    }

    private fun getScreenWidthAndHeight() {
        val display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = resources.displayMetrics.density
        screenHeight = outMetrics.heightPixels + 1
        screenWidth = outMetrics.widthPixels + 1
    }

    private fun loadInitialActivity() {
        val prefManager = PrefManager(this)
        BaseUtils.DEVICE_ID = prefManager.accessToken.toString()
        val intent: Intent = if (prefManager.accessToken.isEmpty()) {
            Intent(this@SplashActivity, AuthenticationActivity::class.java)
        } else {
            Intent(this@SplashActivity, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}