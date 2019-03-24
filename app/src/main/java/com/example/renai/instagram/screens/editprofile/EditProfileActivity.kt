package com.example.renai.instagram.screens.editprofile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.*
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : BaseActivity(), PasswordDialog.Listener {
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mCamera: CameraHelper
    private lateinit var mViewModel: EditProfileViewModel
    private var progressState: Boolean = false

    companion object {
        const val TAG = "EditProfileActivity"
        const val REQUEST_CAMERA_CODE = 13
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        mCamera = CameraHelper(this)
        save_image.setOnClickListener { updateProfile() }
        back_image.setOnClickListener { finish() }
        change_photo_text.setOnClickListener { mCamera.takeCameraPicture() }
        ArrayAdapter.createFromResource(this, R.array.genders_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                gender_selector.adapter = adapter
            }

        setupAuthGuard {
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
                    if (mUser.gender != null) {
                        when {
                            mUser.gender == "Male" -> gender_selector.setSelection(1)
                            mUser.gender == "Female" -> gender_selector.setSelection(2)
                            else -> gender_selector.setSelection(0)
                        }
                    }
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.uploadAndSetPhoto(mCamera.imageUri!!)
        }
    }

    private fun updateProfile() {
        setProgressState()

        mPendingUser = readInputs()
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                PasswordDialog().show(supportFragmentManager, "Password_dialog")
            }
        } else {
            setProgressState()
            showToast(error)
        }
    }

    private fun readInputs(): User {
        val gender: String? =
            if (gender_selector.selectedItem.toString() == "Not Specified") {
                null
            } else {
                gender_selector.selectedItem.toString()
            }

        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            email = email_input.text.toString(),
            bio = bio_input.text.toStringOrNull(),
            website = website_input.text.toStringOrNull(),
            phone = phone_input.text.toString().toLongOrNull(),
            gender = gender
        )
    }

    private fun updateUser(user: User) {
        mViewModel.updateUserProfile(currentUser = mUser, newUser = user)
            .addOnSuccessListener {
                setProgressState()
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
            !user.website.isValidUrl() -> "Please enter valid website"
            else -> null
        }

    private fun setProgressState() {
        if (progressState) {
            save_image.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
            progressState = false
        } else {
            progress_bar.visibility = View.VISIBLE
            save_image.visibility = View.GONE
            progressState = true
        }
    }
}

private fun String?.isValidUrl(): Boolean {
    this?.let {
        val pattern = Patterns.WEB_URL
        val matcher = pattern.matcher(this.toLowerCase())
        return matcher.matches()
    }
    return true
}





