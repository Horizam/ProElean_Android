package com.horizam.pro.elean.ui.main.view.activities

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.horizam.pro.elean.databinding.ActivityAuthenticationBinding
import android.view.Window

import androidx.core.content.ContextCompat

import android.view.WindowManager
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.horizam.pro.elean.R
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.simple_spinner_dropdown_item.*


class AuthenticationActivity : AppCompatActivity(), GenericHandler {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorOne)
    }

    private fun initViews() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun showProgressBar(show: Boolean) {
        binding.progressLayout.isVisible = show
    }

    override fun showErrorMessage(message: String) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        )
        val snackBarView = snackbar.view
        val tvMessage =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tvMessage.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tvMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackBarView.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.color_red,
                null
            )
        )
        snackbar.show()
    }

    override fun showSuccessMessage(message: String) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        )
        val snackBarView = snackbar.view
        val tvMessage =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tvMessage.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tvMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackBarView.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.color_green,
                null
            )
        )
        snackbar.show()
    }

    override fun showNoInternet(show: Boolean) {

    }

}