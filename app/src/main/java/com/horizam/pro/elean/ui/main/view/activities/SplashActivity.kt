package com.horizam.pro.elean.ui.main.view.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.BaseUtils.Companion.screenHeight
import com.horizam.pro.elean.utils.BaseUtils.Companion.screenWidth
import com.horizam.pro.elean.utils.PrefManager
import java.util.*

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)


            var manager: PrefManager = PrefManager(App.getAppContext()!!)
            if (manager.setLanguage == "0") {
                setLocal("en")
            } else {
                setLocal("fi")
            }
        }
    private fun setLocal(lang: String, ) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(
            configuration,
            baseContext.resources.displayMetrics
        )

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
            } else if (intent.getStringExtra(Constants.TYPE).toString() == Constants.ORDER) {
                val contentID = intent.getStringExtra(Constants.CONTENT_ID)
                bundle.putString(
                    Constants.TYPE,
                    intent.getStringExtra(Constants.TYPE).toString()
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    contentID
                )
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            } else if (intent.getStringExtra(Constants.TYPE).toString() == Constants.TYPE_OFFER) {
                val contentID = intent.getStringExtra(Constants.CONTENT_ID)
                bundle.putString(
                    Constants.TYPE,
                    intent.getStringExtra(Constants.TYPE).toString()
                )
                bundle.putString(
                    Constants.CONTENT_ID,
                    contentID
                )
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            } else {
                loadInitialActivity()
                getScreenWidthAndHeight()
            }
        } else {
            loadInitialActivity()
            getScreenWidthAndHeight()
        }
    }

    private val listener: InstallStateUpdatedListener? =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                Log.d("mytag", "An update has been downloaded")

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
            Intent(this@SplashActivity, HomeActivity::class.java)
        } else {
            Intent(this@SplashActivity, HomeActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}