package com.example.renai.instagram.screens

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.renai.instagram.R
import com.example.renai.instagram.common.SingleLiveEvent
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.CommonViewModel

class RegisterViewModel(
    private val usersRepository: FirebaseUsersRepository,
    private val commonViewModel: CommonViewModel,
    private val app: Application
) : ViewModel() {
    private var email: String? = null
    private val _goToNamePassScreen = SingleLiveEvent<Unit>()
    val goToNamePassScreen = _goToNamePassScreen

    fun onEmailEntered(email: String) {
        if (email.isNotEmpty()) {
            this.email = email
            usersRepository.isUserExistsForEmail(email).addOnSuccessListener { exists ->
                if (!exists) {
                    _goToNamePassScreen.call()
                } else {
                    commonViewModel.setErrorMessage(app.getString(R.string.this_email_is_already_exists))
                }
            }
        } else {
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_your_email))
        }
    }

    fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            val localEmail = email
            if (localEmail != null) {
                usersRepository.createUser(mkUser(fullName, localEmail), password)
                mAuth.createUserWithEmailAndPassword(localEmail, password) {
                    mDatabase.createUser(it.user.uid, mkUser(fullName, localEmail)) {
                        startHomeActivity()
                    }
                }
            } else {
                Log.e(RegisterActivity.TAG, "onRegister: email is null")
                showToast(getString(R.string.please_enter_your_email))
                supportFragmentManager.popBackStack()
            }
        } else {
            showToast(getString(R.string.please_enter_full_name_and_password))
        }
    }
    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
        fullName.toLowerCase().replace(" ", ".")

}

