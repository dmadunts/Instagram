package com.example.renai.instagram.screens

import android.app.Application
import com.example.renai.instagram.common.firebase.FirebaseAuthManager
import com.example.renai.instagram.data.firebase.FirebaseFeedPostsRepository
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository

class InstagramApp : Application() {
    val usersRepository by lazy { FirebaseUsersRepository() }
    val feedPostsRepository by lazy { FirebaseFeedPostsRepository() }
    val authManager by lazy { FirebaseAuthManager() }

}