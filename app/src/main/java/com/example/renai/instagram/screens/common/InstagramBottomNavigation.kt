package com.example.renai.instagram.screens.common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.renai.instagram.R
import com.example.renai.instagram.models.Notification
import com.example.renai.instagram.models.NotificationType
import com.example.renai.instagram.screens.home.HomeActivity
import com.example.renai.instagram.screens.notifications.NotificationsActivity
import com.example.renai.instagram.screens.notifications.NotificationsViewModel
import com.example.renai.instagram.screens.profile.ProfileActivity
import com.example.renai.instagram.screens.search.SearchActivity
import com.example.renai.instagram.screens.share.ShareActivity
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.nhaarman.supertooltips.ToolTip
import com.nhaarman.supertooltips.ToolTipRelativeLayout
import com.nhaarman.supertooltips.ToolTipView
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import kotlinx.android.synthetic.main.notifications_tooltip_content.view.*

@Suppress("DEPRECATION")
class InstagramBottomNavigation(
    private val uid: String,
    private val tooltipLayout: ToolTipRelativeLayout,
    private val bnv: BottomNavigationViewEx,
    private val navNumber: Int,
    private val activity: BaseActivity
) : LifecycleObserver {
    private lateinit var mViewModel: NotificationsViewModel
    private lateinit var mNotificationsContentView: View
    private var lastTooltipView: ToolTipView? = null

    companion object {
        const val NOTIFICATION_ICON_POS = 3
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        mViewModel = activity.initViewModel()
        mViewModel.init(uid)
        mViewModel.notifications.observe(activity, Observer {
            it?.let { showNotifications(it) }
        })
        mNotificationsContentView = activity.layoutInflater.inflate(R.layout.notifications_tooltip_content, null, false)
    }

    private fun showNotifications(notifications: List<Notification>) {
        if (lastTooltipView != null) {
            val parent = mNotificationsContentView.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(mNotificationsContentView)
                lastTooltipView?.remove()
            }
            lastTooltipView = null
        }
        val newNotifications = notifications.filter { !it.read }
        val newNotificationsMap = newNotifications
            .groupBy { it.type }
            .mapValues { (_, values) -> values.size }

        fun setCount(image: ImageView, textView: TextView, type: NotificationType) {
            val count = newNotificationsMap[type] ?: 0
            if (count == 0) {
                image.visibility = View.GONE
                textView.visibility = View.GONE
            } else {
                image.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                textView.text = count.toString()
            }
        }
        with(mNotificationsContentView) {
            setCount(likes_image, likes_count_text, NotificationType.Like)
            setCount(follows_image, follows_count_text, NotificationType.Follow)
            setCount(comments_image, comments_count_text, NotificationType.Comment)
        }

        if (newNotifications.isNotEmpty()) {
            val tooltip = ToolTip()
                .withColor(ContextCompat.getColor(activity, R.color.red))
                .withContentView(mNotificationsContentView)
                .withAnimationType(ToolTip.AnimationType.FROM_TOP)
                .withShadow()
            lastTooltipView = tooltipLayout.showToolTipForView(
                tooltip, bnv.getIconAt(
                    NOTIFICATION_ICON_POS
                )
            )
            lastTooltipView?.setOnClickListener {
                mViewModel.setNotificationsAsRead(newNotifications)
                bnv.getBottomNavigationItemView(NOTIFICATION_ICON_POS).callOnClick()
            }
        }
    }

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
                    R.id.nav_item_likes -> NotificationsActivity::class.java
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
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                activity.startActivity(intent)
                activity.overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }
}

fun BaseActivity.setupBottomNavigation(uid: String, navNumber: Int) {
    val bnv = InstagramBottomNavigation(uid, tooltip_layout, bottom_navigation_view, navNumber, this)
    this.lifecycle.addObserver(bnv)
}