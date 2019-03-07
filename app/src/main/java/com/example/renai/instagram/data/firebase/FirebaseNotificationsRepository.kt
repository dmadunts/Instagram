package com.example.renai.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.common.toUnit
import com.example.renai.instagram.data.NotificationsRepository
import com.example.renai.instagram.data.common.map
import com.example.renai.instagram.data.firebase.common.FirebaseLiveData
import com.example.renai.instagram.data.firebase.common.database
import com.example.renai.instagram.models.Notification
import com.google.android.gms.tasks.Task

class FirebaseNotificationsRepository() : NotificationsRepository {
    override fun createNotifications(uid: String, notification: Notification): Task<Unit> =
        notificationRef(uid).push().setValue(notification).toUnit()

    override fun getNotifications(uid: String): LiveData<List<Notification>> =
        FirebaseLiveData(notificationRef(uid)).map {
            it.children.map { it.asNotification() }
        }

    override fun setNotificationsAsRead(ids: List<String>, read: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun notificationRef(uid: String) =
        database.child("notification").push().child(uid)
}