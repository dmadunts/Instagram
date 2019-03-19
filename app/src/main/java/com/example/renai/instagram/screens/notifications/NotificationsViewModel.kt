package com.example.renai.instagram.screens.notifications

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.data.NotificationsRepository
import com.example.renai.instagram.data.UsersRepository
import com.example.renai.instagram.models.Notification
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class NotificationsViewModel(
    private val usersRepository: UsersRepository,
    private val notificationsRepository: NotificationsRepository,
    onFailureListener: OnFailureListener
) : BaseViewModel(onFailureListener) {
    lateinit var notifications: LiveData<List<Notification>>
    private lateinit var uid: String
    var user = usersRepository.getUser()

    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid
            notifications = notificationsRepository.getNotifications(uid)
        }
    }

    fun setNotificationsAsRead(notifications: List<Notification>) {
        val ids = notifications.filter { !it.read }.map { it.id }
        if (ids.isNotEmpty()) {
            notificationsRepository.setNotificationsAsRead(uid, ids, true)
                .addOnFailureListener(onFailureListener)
        }
    }

}
