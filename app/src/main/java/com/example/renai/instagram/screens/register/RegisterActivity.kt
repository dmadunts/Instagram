package com.example.renai.instagram.screens.register

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.home.HomeActivity

class RegisterActivity : BaseActivity(), EmailFragment.Listener,
    NamePassFragment.Listener {
    companion object {
        const val TAG = "RegisterActivity"
    }

    private lateinit var mViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG, "onCreate")

        mViewModel = initViewModel()
        Log.d(TAG, "onCreate: initViewModel")
        mViewModel.goToNamePassScreen.observe(this, Observer {
            supportFragmentManager.beginTransaction().replace(
                R.id.frame_layout,
                NamePassFragment()
            )
                .addToBackStack(null)
                .commit()
        })
        Log.d(TAG, "onCreate: goToNamePass")

        mViewModel.goToHomeScreen.observe(this, Observer {
            startHomeActivity()
        })
        Log.d(TAG, "onCreate: goToHomeScreen")

        mViewModel.goBackToEmailScreen.observe(this, Observer {
            supportFragmentManager.popBackStack()
        })
        Log.d(TAG, "onCreate: goToBackToEmailScreen")


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
        }
        Log.d(TAG, "onCreate: the end")

    }

    override fun onNext(email: String) = mViewModel.onEmailEntered(email)

    override fun onRegister(fullName: String, password: String) = mViewModel.onRegister(fullName, password)

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}


