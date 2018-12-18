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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "EditProfileActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

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
                        val user = it.getValue(User::class.java)
                        name_input.setText(user!!.name, TextView.BufferType.EDITABLE)
                        username_input.setText(user.username, TextView.BufferType.EDITABLE)
                        bio_input.setText(user.bio, TextView.BufferType.EDITABLE)
                        website_input.setText(user.website, TextView.BufferType.EDITABLE)
                        phone_number_input.setText(user.phone.toString(), TextView.BufferType.EDITABLE)
                        email_input.setText(user.email, TextView.BufferType.EDITABLE)
                    })
            }
    }
}

