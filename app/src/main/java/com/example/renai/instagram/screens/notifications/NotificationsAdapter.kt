package com.example.renai.instagram.screens.notifications

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renai.instagram.R
import com.example.renai.instagram.common.SimpleCallback
import com.example.renai.instagram.common.formatRelativeTimestamp
import com.example.renai.instagram.models.Notification
import com.example.renai.instagram.models.NotificationType
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.loadImageOrHide
import com.example.renai.instagram.screens.common.loadUserPhoto
import com.example.renai.instagram.screens.common.setCaptionText
import kotlinx.android.synthetic.main.notification_item.view.*
import java.util.*

class NotificationsAdapter(val currentUser: User) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var notifications = listOf<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }


    //TODO Needs refactor
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        with(holder.view) {
            user_photo.loadUserPhoto(currentUser.photo)
            val notificationText =
                when (notification.type) {
                    NotificationType.Like -> context.getString(R.string.liked_your_post)
                    NotificationType.Comment -> context.getString(R.string.commented, notification.commentText)
                    NotificationType.Follow -> context.getString(R.string.started_following_you)
                }
            notification_text.setCaptionText(currentUser.username, notificationText)
            post_image.loadImageOrHide(notification.postImage)
            time.text = formatRelativeTimestamp(notification.timestampDate(), Date())
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun updateNotifications(newNotifications: List<Notification>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(notifications, newNotifications) { it.id })
        this.notifications = newNotifications
        diffResult.dispatchUpdatesTo(this)
    }

}
