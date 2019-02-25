package com.example.renai.instagram.screens.share

import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.models.FeedPost
import com.example.renai.instagram.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

class ShareViewModel(private val usersRepository: FirebaseUsersRepository,
                     private val onFailureListener: OnFailureListener) : ViewModel() {
    val user = usersRepository.getUser()

    fun share(user: User, caption: String, imageUri: Uri?) {
        if (imageUri != null) {
            usersRepository.uploadUserImage(user.uid, imageUri).onSuccessTask { downloadUrl ->
                Tasks.whenAll(
                usersRepository.setUserImage(user.uid, downloadUrl!!),
                    usersRepository.createFeedPost(user.uid, makeFeedPost(user, caption, downloadUrl.toString()))
                )
            }.addOnFailureListener(onFailureListener)
        }
    }

    private fun makeFeedPost(user: User, caption: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
            uid = user.uid,
            username = user.username,
            image = imageDownloadUrl.toString(),
            caption = caption,
            photo = user.photo
        )
    }
}
