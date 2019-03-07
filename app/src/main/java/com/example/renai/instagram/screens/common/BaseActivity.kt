package com.example.renai.instagram.screens.common

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.renai.instagram.screens.InstagramApp
import com.example.renai.instagram.screens.login.LoginActivity

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var commonViewModel: CommonViewModel

    companion object {
        const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        commonViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
        commonViewModel.errorMessage.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
        Log.d("BaseActivity", "onCreate")
    }

    protected inline fun <reified T : BaseViewModel> initViewModel(): T =
        ViewModelProviders.of(
            this, ViewModelFactory(
                application as InstagramApp,
                commonViewModel = commonViewModel,
                onFailureListener = commonViewModel
            )
        ).get(T::class.java)

    fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        Log.d("BaseActivity", "goToLogin")
    }
}
