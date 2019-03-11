package com.example.renai.instagram.data

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.models.Notification
import com.google.android.gms.tasks.Task

interface NotificationsRepository {
    fun createNotification(uid: String, notification: Notification): Task<Unit>
    fun getNotifications(uid: String): LiveData<List<Notification>>
    fun setNotificationsAsRead(uid:String, ids: List<String>, read: Boolean):Task<Unit>
}