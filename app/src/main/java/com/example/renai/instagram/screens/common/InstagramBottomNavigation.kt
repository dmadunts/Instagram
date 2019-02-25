package com.example.renai.instagram.screens.common

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.util.Log
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.home.HomeActivity
import com.example.renai.instagram.screens.likes.LikesActivity
import com.example.renai.instagram.screens.profile.ProfileActivity
import com.example.renai.instagram.screens.search.SearchActivity
import com.example.renai.instagram.screens.share.ShareActivity
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import kotlinx.android.synthetic.main.bottom_navigation_view.*

@Suppress("DEPRECATION")
class InstagramBottomNavigation(
    private val bnv: BottomNavigationViewEx, private val navNumber: Int,
    activity: Activity
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        bnv.menu.getItem(navNumber).isChecked = true
    }

    init {
        bnv.setIconSize(29f, 29f)
        bnv.setTextVisibility(false)
        bnv.enableAnimation(false)
        bnv.enableShiftingMode(false)
        bnv.enableItemShiftingMode(false)
        for (i in 0 until bnv.menu.size()) {
            bnv.setIconTintList(i, null)
        }
        bnv.setOnNavigationItemSelectedListener {
            val nextActivity =
                when (it.itemId) {
                    R.id.nav_item_home -> HomeActivity::class.java
                    R.id.nav_item_likes -> LikesActivity::class.java
                    R.id.nav_item_profile -> ProfileActivity::class.java
                    R.id.nav_item_search -> SearchActivity::class.java
                    R.id.nav_item_share -> ShareActivity::class.java
                    else -> {
                        Log.e(BaseActivity.TAG, "Unknown nav item clicked: $it")
                        null
                    }
                }
            if (nextActivity != null) {
                val intent = Intent(activity, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                activity.startActivity(intent)
                activity.overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }
}

fun BaseActivity.setupBottomNavigation(navNumber: Int) {
    val bnv = InstagramBottomNavigation(bottom_navigation_view, navNumber, this)
    this.lifecycle.addObserver(bnv)
}