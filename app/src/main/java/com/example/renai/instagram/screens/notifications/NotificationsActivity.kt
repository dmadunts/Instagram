package com.example.renai.instagram.screens.notifications

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.setupAuthGuard
import com.example.renai.instagram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_notifications.*

class NotificationsActivity : BaseActivity() {
    private lateinit var mAdapter: NotificationsAdapter
    private lateinit var mViewModel: NotificationsViewModel

    companion object {
        const val TAG = "LikesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setupBottomNavigation(3)

        setupAuthGuard { uid ->
            mViewModel = initViewModel()
            mViewModel.init(uid)
            mViewModel.notifications.observe(this, Observer {
                it?.let {
                    mAdapter.updateNotifications(it)
                    mViewModel.setNotificationsAsRead(it)
                }
            })

            mAdapter = NotificationsAdapter()
            notifications_recycler.layoutManager = LinearLayoutManager(this)
            notifications_recycler.adapter = mAdapter
        }
    }
}
