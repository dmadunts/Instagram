package com.example.renai.instagram.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.views.PasswordDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        save_image.setOnClickListener { updateProfile() }
        close_image.setOnClickListener { finish() }

        //Spinner
        val spinner: Spinner = findViewById(R.id.edit_profile_spinner)
        ArrayAdapter.createFromResource(this, R.array.genders_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                mAuth = FirebaseAuth.getInstance()
                mDatabase = FirebaseDatabase.getInstance().reference

                mDatabase.child("users").child(mAuth.currentUser!!.uid)
                    .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                        mUser = it.getValue(User::class.java)!!
                        name_input.setText(mUser.name, TextView.BufferType.EDITABLE)
                        username_input.setText(mUser.username, TextView.BufferType.EDITABLE)
                        bio_input.setText(mUser.bio, TextView.BufferType.EDITABLE)
                        website_input.setText(mUser.website, TextView.BufferType.EDITABLE)
                        phone_input.setText(mUser.phone.toString(), TextView.BufferType.EDITABLE)
                        email_input.setText(mUser.email, TextView.BufferType.EDITABLE)
                    })
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
        val phoneStr = phone_input.text.toString()
        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            bio = bio_input.text.toString(),
            website = website_input.text.toString(),
            phone = if (phoneStr.isEmpty()) 0 else phoneStr.toLong(),
            email = email_input.text.toString()
        )
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.website != mUser.website) updatesMap["website"] = user.website

        mDatabase.updateUser(mAuth.currentUser!!.uid, updatesMap) {
            showToast("Profile saved.")
            finish()
        }
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential) {
                mAuth.currentUser!!.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else {
            showToast("You should enter your password.")
        }
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> "Please enter your Name"
            user.username.isEmpty() -> "Please enter your Username"
            user.email.isEmpty() -> "Please enter your E-mail"
            else -> null
        }

    private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit) {
        updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun DatabaseReference.updateUser(uid: String, updatesMap: Map<String, Any>, onSuccess: () -> Unit) {
        child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
    }
}

