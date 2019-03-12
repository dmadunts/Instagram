package com.example.renai.instagram.screens

import android.app.Application
import com.example.renai.instagram.common.firebase.FirebaseAuthManager
import com.example.renai.instagram.data.firebase.FirebaseFeedPostsRepository
import com.example.renai.instagram.data.firebase.FirebaseNotificationsRepository
import com.example.renai.instagram.data.firebase.FirebaseSearchRepository
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.screens.notifications.NotificationsCreator
import com.example.renai.instagram.screens.search.SearchPostCreator

class InstagramApp : Application() {
    val usersRepository by lazy { FirebaseUsersRepository() }
    val feedPostsRepository by lazy { FirebaseFeedPostsRepository() }
    val authManager by lazy { FirebaseAuthManager() }
    val notificationsRepository by lazy { FirebaseNotificationsRepository() }
    val searchPostsRepository by lazy { FirebaseSearchRepository() }

    override fun onCreate() {
        super.onCreate()
        NotificationsCreator(notificationsRepository, usersRepository, feedPostsRepository)
        SearchPostCreator(searchPostsRepository)
    }
}