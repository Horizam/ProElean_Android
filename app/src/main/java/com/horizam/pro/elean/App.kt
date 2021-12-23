package com.horizam.pro.elean

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.stripe.android.PaymentConfiguration
import io.paperdb.Paper

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        context = applicationContext
        setStripeConfig()
        Paper.init(getApplicationContext());
    }

    private fun setStripeConfig() {
        PaymentConfiguration.init(
            applicationContext,
            Constants.STRIPE_PUBLISH_KEY
        )
    }

    companion object {

        private var context: Context? = null

        fun getAppContext(): Context? {
            return context
        }

    }
}