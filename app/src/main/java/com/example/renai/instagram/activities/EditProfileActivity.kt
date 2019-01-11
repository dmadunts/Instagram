package com.example.renai.instagram.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.CameraHelper
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import com.example.renai.instagram.views.PasswordDialog
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mCamera: CameraHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = CameraHelper(this)
        mFirebase = FirebaseHelper(this)
        save_image.setOnClickListener { updateProfile() }
        back_image.setOnClickListener { finish() }
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }

        //Spinner
        val spinner: Spinner = findViewById(R.id.edit_profile_spinner)
        ArrayAdapter.createFromResource(this, R.array.genders_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter


                mFirebase.currentUserReference()
                    .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                        mUser = it.getValue(User::class.java)!!
                        name_input.setText(mUser.name)
                        username_input.setText(mUser.username)
                        bio_input.setText(mUser.bio)
                        website_input.setText(mUser.website)
                        phone_input.setText(mUser.phone?.toString())
                        email_input.setText(mUser.email)
                        profile_image.loadUserPhoto(mUser.photo)
                    })
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mFirebase.uploadUserPhoto(mCamera.imageUri!!) {
                mFirebase.updateUserPhoto { photoUrl ->
                    mUser = mUser.copy(photo = photoUrl)
                    profile_image.loadUserPhoto(mUser.photo)
                }
            }
        }
    }

    private fun updateProfile() {
        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "Password_dialog")
            }
        } else {
            showToast(error)
        }
    }

    private fun readInputs(): User {
        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            email = email_input.text.toString(),
            bio = bio_input.text.toStringOrNull(),
            website = website_input.text.toStringOrNull(),
            phone = phone_input.text.toString().toLongOrNull()
        )
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.website != mUser.website) updatesMap["website"] = user.website

        mFirebase.updateUser(updatesMap) {
            finish()
        }
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebase.reauthenticate(credential) {
                mFirebase.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else {
            showToast("You should enter your password.")
        }
    }
}


private fun validate(user: User): String? =
    when {
        user.name.isEmpty() -> "Please enter your Name"
        user.username.isEmpty() -> "Please enter your Username"
        user.email.isEmpty() -> "Please enter your E-mail"
        else -> null
    }



