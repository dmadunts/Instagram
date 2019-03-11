package com.example.renai.instagram.screens.notifications

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.data.NotificationsRepository
import com.example.renai.instagram.data.firebase.FirebaseNotificationsRepository
import com.example.renai.instagram.models.Notification
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository,
    onFailureListener: OnFailureListener
) : BaseViewModel(onFailureListener) {
    lateinit var notifications: LiveData<List<Notification>>
    private lateinit var uid: String


    fun init(uid: String) {
        this.uid = uid
        notifications = notificationsRepository.getNotifications(uid)
    }

    fun setNotificationsAsRead(notifications: List<Notification>) {
        val ids = notifications.filter { !it.read }.map { it.id }
        if (ids.isNotEmpty()) {
            notificationsRepository.setNotificationsAsRead(uid, ids, true)
                .addOnFailureListener(onFailureListener)
        }
    }

}
