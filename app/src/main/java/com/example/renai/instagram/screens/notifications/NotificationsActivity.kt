package com.example.renai.instagram.screens.notifications

import android.os.Bundle
import android.util.Log
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.setupBottomNavigation

class NotificationsActivity : BaseActivity() {
    companion object {
        const val TAG = "LikesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications_activity)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate")
    }
}
