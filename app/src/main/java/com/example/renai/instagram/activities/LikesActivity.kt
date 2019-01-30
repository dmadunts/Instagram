package com.example.renai.instagram.activities

import android.os.Bundle
import android.util.Log
import com.example.renai.instagram.R
import com.example.renai.instagram.views.setupBottomNavigation

class LikesActivity : BaseActivity() {
    companion object {
        const val TAG = "LikesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate")
    }
}
