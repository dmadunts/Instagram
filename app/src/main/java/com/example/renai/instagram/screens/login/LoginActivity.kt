package com.example.renai.instagram.screens.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.coordinateBtnAndInputs
import com.example.renai.instagram.screens.home.HomeActivity
import com.example.renai.instagram.screens.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity(), KeyboardVisibilityEventListener, View.OnClickListener {
    private lateinit var mViewModel: LoginViewModel

    companion object {
        const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mViewModel = initViewModel()
        mViewModel.goToHomeScreen.observe(this, Observer {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        })
        mViewModel.goToRegisterScreen.observe(this, Observer {
            startActivity(Intent(this, RegisterActivity::class.java))
        })

        KeyboardVisibilityEvent.setEventListener(this, this)

        coordinateBtnAndInputs(login_btn, email_input, password_input)

        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

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
