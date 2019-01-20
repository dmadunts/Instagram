package com.example.renai.instagram.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.renai.instagram.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        //Listener for keyboard open/close state.
        //Used in conjunction with scrollview in case of currentUser focused TextEdits
        // to resize window and let Buttons stay reachable
        KeyboardVisibilityEvent.setEventListener(this,this)

        //Method-StateChanger for buttons
        coordinateBtnAndInputs(login_btn, email_input, password_input)

        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

        //Instance for FirebaseAuth
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_btn -> {
                val email = email_input.text.toString()
                val password = password_input.text.toString()
                if (validate(email, password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            showToast(it.exception!!.message!!)
                        }
                    }
                } else {
                    showToast(getString(R.string.please_enter_email_and_password))
                }
            }
            R.id.create_account_text -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
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

    private fun validate(email: String, password: String) =
        email.isNotEmpty() && password.isNotEmpty()
}
