package com.example.renai.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.common.toUnit
import com.example.renai.instagram.data.NotificationsRepository
import com.example.renai.instagram.data.common.map
import com.example.renai.instagram.data.firebase.common.FirebaseLiveData
import com.example.renai.instagram.data.firebase.common.database
import com.example.renai.instagram.models.Notification
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot

class FirebaseNotificationsRepository : NotificationsRepository {
    override fun createNotification(uid: String, notification: Notification): Task<Unit> =
        notificationRef(uid).push().setValue(notification).toUnit()

    override fun getNotifications(uid: String): LiveData<List<Notification>> =
        FirebaseLiveData(notificationRef(uid)).map {
            it.children.map { it.asNotification()!! }
        }

    override fun setNotificationsAsRead(uid: String, ids: List<String>, read: Boolean): Task<Unit> {
        val updatesMap = ids.map { "$it/read" to read }.toMap()
        return notificationRef(uid).updateChildren(updatesMap).toUnit()
    }

    private fun notificationRef(uid: String) =
        database.child("notifications").child(uid)

    private fun DataSnapshot.asNotification() = getValue(Notification::class.java)?.copy(id = key!!)
}