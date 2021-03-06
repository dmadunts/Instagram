package com.example.renai.instagram.screens.notifications

import android.arch.lifecycle.Observer
import android.util.Log
import com.example.renai.instagram.common.BaseEventListener
import com.example.renai.instagram.common.Event
import com.example.renai.instagram.common.EventBus
import com.example.renai.instagram.data.FeedPostsRepository
import com.example.renai.instagram.data.NotificationsRepository
import com.example.renai.instagram.data.UsersRepository
import com.example.renai.instagram.data.common.observeFirstNotNull
import com.example.renai.instagram.models.Notification
import com.example.renai.instagram.models.NotificationType

class NotificationsCreator(
    private val notificationsRepository: NotificationsRepository,
    private val usersRepository: UsersRepository,
    private val feedPostsRepository: FeedPostsRepository
) : BaseEventListener() {
    init {
        EventBus.events.observe(this, Observer {
            it?.let { event ->
                when (event) {
                    is Event.CreateFollow -> {
                        usersRepository.getUser(event.userUid).observeFirstNotNull(this) { user ->
                            val notification = Notification(
                                uid = user.uid,
                                username = user.username,
                                photo = user.photo,
                                type = NotificationType.Follow
                            )
                            notificationsRepository.createNotification(event.followUid, notification)
                                .addOnFailureListener { Log.d(TAG, "Failed to create notification", it) }
                        }
                    }

                    //TODO Needs refactor: add observeFirstNotNull
                    is Event.CreateLike -> {
                        Log.d("NotificationCreator", "Notification observed")

                        val user = usersRepository.getUser(event.uid).value
                        val post = feedPostsRepository.getFeedPost(event.uid, event.postId).value
                        if (user != null && post != null) {

                            val notification = Notification(
                                uid = user.uid,
                                username = user.username,
                                photo = user.photo,
                                postId = post.id,
                                postImage = post.image,
                                type = NotificationType.Like
                            )
                            notificationsRepository.createNotification(post.uid, notification)
                                .addOnFailureListener { Log.d(TAG, "Failed to create notification", it) }
                            Log.d("NotificationCreator", "Notification created")
                        }

                        //TODO look at this later, may cause problems with NotificationsActivity
                        //I removed this code because it make FeedPost's heart-animation freeze
                        //Seems like this mapping was needed to guarantee user's data
                        //and post data not null, cuz data request to Database not always have time to return a value.
                        //link https://firebase.google.com/docs/database/android/read-and-write

//                                userData.zip(postData).observeFirstNotNull(this) { (user, post) ->
//                                    val notification = Notification(
//                                        uid = user.uid,
//                                        username = user.username,
//                                        photo = user.photo,
//                                        postId = post.id,
//                                        postImage = post.image,
//                                        type = NotificationType.Like
//                                    )
//                                    notificationsRepository.createNotification(post.uid, notification)
//                                        .addOnFailureListener { Log.d(TAG, "Failed to create notification", it) }
//                                    Log.d("NotificationCreator", "Notification created")
//                                }
                    }
                    is Event.CreateComment -> {
                        feedPostsRepository.getFeedPost(event.comment.uid, event.postId)
                            .observeFirstNotNull(this) { post ->
                                val notification = Notification(
                                    uid = event.comment.uid,
                                    username = event.comment.username,
                                    photo = event.comment.photo,
                                    postId = event.postId,
                                    postImage = post.image,
                                    commentText = event.comment.text,
                                    type = NotificationType.Comment
                                )
                                notificationsRepository.createNotification(post.uid, notification)
                                    .addOnFailureListener { Log.d(TAG, "Failed to create notification", it) }
                            }
                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "NotificationsCreator"
    }
}