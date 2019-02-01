package com.example.renai.instagram.activities.editprofile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.renai.instagram.R
import com.example.renai.instagram.activities.BaseActivity
import com.example.renai.instagram.activities.loadUserPhoto
import com.example.renai.instagram.activities.showToast
import com.example.renai.instagram.activities.toStringOrNull
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.CameraHelper
import com.example.renai.instagram.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity(), PasswordDialog.Listener {
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mCamera: CameraHelper
    private lateinit var mViewModel: EditProfileViewModel

    companion object {
        const val TAG = "EditProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = CameraHelper(this)
        save_image.setOnClickListener { updateProfile() }
        back_image.setOnClickListener { finish() }
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }

        mViewModel = initViewModel()

        mViewModel.user.observe(this, Observer {
            it?.let {
                mUser = it
                name_input.setText(mUser.name)
                username_input.setText(mUser.username)
                bio_input.setText(mUser.bio)
                website_input.setText(mUser.website)
                phone_input.setText(mUser.phone?.toString())
                email_input.setText(mUser.email)
                profile_image.loadUserPhoto(mUser.photo)
            }
        })
        //Spinner
        val spinner: Spinner = findViewById(R.id.edit_profile_spinner)
        ArrayAdapter.createFromResource(this, R.array.genders_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.uploadAndSetPhoto(mCamera.imageUri!!)
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
        mViewModel.updateUserProfile(currentUser = mUser, newUser = user)
            .addOnSuccessListener {
                showToast("Profile saved")
                finish()
            }
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            mViewModel.updateEmail(
                currentEmail = mUser.email,
                newEmail = mPendingUser.email,
                password = password
            )
                .addOnSuccessListener { updateUser(mPendingUser) }
        }
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> getString(R.string.please_enter_your_name)
            user.username.isEmpty() -> getString(R.string.please_enter_your_username)
            user.email.isEmpty() -> getString(R.string.please_enter_your_email)
            else -> null
        }
}





