package com.example.renai.instagram.screens.share

import android.net.Uri
import com.example.renai.instagram.common.SingleLiveEvent
import com.example.renai.instagram.data.firebase.FirebaseFeedPostsRepository
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.models.FeedPost
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks

class ShareViewModel(
    private val usersRepository: FirebaseUsersRepository,
    private val feedPostsRepository: FirebaseFeedPostsRepository,
    onFailureListener: OnFailureListener
) : BaseViewModel(onFailureListener) {
    val user = usersRepository.getUser()
    private val _shareCompletedEvent = SingleLiveEvent<Unit>()
    val shareComletedEvent = _shareCompletedEvent

    private val _shareStartedEvent = SingleLiveEvent<Unit>()
    val shareStartedEvent = _shareStartedEvent

    fun share(user: User, caption: String, imageUri: Uri?) {
        _shareStartedEvent.call()
        if (imageUri != null) {
            usersRepository.uploadUserImage(user.uid, imageUri).onSuccessTask { downloadUrl ->
                Tasks.whenAll(
                    usersRepository.setUserImage(user.uid, downloadUrl!!),
                    feedPostsRepository.createFeedPost(
                        user.uid,
                        makeFeedPost(user, caption, downloadUrl.toString())
                    )
                ).addOnCompleteListener {
                    _shareCompletedEvent.call()
                }
            }.addOnFailureListener(onFailureListener)
        }
    }

    private fun makeFeedPost(user: User, caption: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
            uid = user.uid,
            username = user.username,
            image = imageDownloadUrl,
            caption = caption,
            photo = user.photo
        )
    }
}

