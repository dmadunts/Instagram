package com.example.renai.instagram.screens.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.register.RegisterActivity
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.coordinateBtnAndInputs
import com.example.renai.instagram.screens.common.setupAuthGuard
import com.example.renai.instagram.screens.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity(), KeyboardVisibilityEventListener, View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mViewModel: LoginViewModel

    companion object {
        const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        //Listener for keyboard open/close state.
        //Used in conjunction with scrollview in case of currentUser focused TextEdits
        // to resize window and let Buttons stay reachable
        KeyboardVisibilityEvent.setEventListener(this, this)

        //Method-StateChanger for buttons
        coordinateBtnAndInputs(login_btn, email_input, password_input)

        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

        setupAuthGuard {
            mViewModel = initViewModel()
            mViewModel.goToHomeScreen.observe(this, Observer {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            })
            mViewModel.goToRegisterScreen.observe(this, Observer {
                startActivity(Intent(this, RegisterActivity::class.java))
            })
            mAuth = FirebaseAuth.getInstance()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_btn -> {
                mViewModel.onLoginClick(
                    email = email_input.text.toString(),
                    password = password_input.text.toString()
                )
            }
            R.id.create_account_text -> mViewModel.onRegisterClick()
        }
    }

    //When keyboard is visible, this one resizes window to let buttons stay visible
    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen) {
            create_account_text.visibility = View.GONE
            login_scrollView.scrollTo(0, login_scrollView.top)
        } else {
            login_scrollView.scrollTo(0, login_scrollView.bottom)
            create_account_text.visibility = View.VISIBLE
        }
    }
}
