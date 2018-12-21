package com.example.renai.instagram.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
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

                val auth = FirebaseAuth.getInstance()
                val database = FirebaseDatabase.getInstance().reference
                database.child("users").child(auth.currentUser!!.uid)
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
        //get user from inputs
        //validate
        val user = User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            bio = bio_input.text.toString(),
            website = website_input.text.toString(),
            phone = phone_input.text.toString().toLong(),
            email = email_input.text.toString()
        )
        val error = validate(user)
        if (error == null) {
            //save user
        } else {
            showToast(error)
        }
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> "Please enter your Name"
            user.username.isEmpty() -> "Please enter your Username"
            user.email.isEmpty() -> "Please enter your E-mail"
            else -> null
        }
}

