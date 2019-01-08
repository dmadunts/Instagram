package com.example.renai.instagram.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.renai.instagram.R
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        mFirebase = FirebaseHelper(this)

        mFirebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }


    }

    override fun onStart() {
        super.onStart()
        val currentUser  = mFirebase.auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mFirebase.database.child("feed-posts").child(currentUser.uid)
                .addValueEventListener(ValueEventListenerAdapter {
                    val posts = it.children.map { it.getValue(FeedPost::class.java)!! }
                    Log.d(TAG, "feedPosts: ${posts.first().timestampDate()}")
                })
        }
    }
}
