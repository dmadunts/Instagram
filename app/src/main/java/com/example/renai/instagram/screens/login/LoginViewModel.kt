package com.example.renai.instagram.screens.login

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.renai.instagram.R
import com.example.renai.instagram.common.AuthManager
import com.example.renai.instagram.common.SingleLiveEvent
import com.example.renai.instagram.screens.common.CommonViewModel
import com.google.android.gms.tasks.OnFailureListener

class LoginViewModel(
    private val authManager: AuthManager,
    private val app: Application,
    private val commonViewModel: CommonViewModel,
    private val onFailureListener: OnFailureListener
) : ViewModel() {
    private val _goToHomeScreen = SingleLiveEvent<Unit>()
    val goToHomeScreen: LiveData<Unit> = _goToHomeScreen

    private val _goToRegisterScreen = SingleLiveEvent<Unit>()
    val goToRegisterScreen = _goToRegisterScreen

    fun onLoginClick(email: String, password: String) {
        if (validate(email, password)) {
            authManager.signIn(email, password).addOnSuccessListener {
                _goToHomeScreen.value = Unit
            }.addOnFailureListener(onFailureListener)
        } else {
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_email_and_password))
        }
    }

    private fun validate(email: String, password: String) =
        email.isNotEmpty() and password.isNotEmpty()

    fun onRegisterClick() {
        _goToRegisterScreen.call()
    }

}