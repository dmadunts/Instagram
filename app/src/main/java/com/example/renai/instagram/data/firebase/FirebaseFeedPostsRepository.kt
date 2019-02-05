package com.example.renai.instagram.data.firebase

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.common.TaskSourceOnCompleteListener
import com.example.renai.instagram.common.ValueEventListenerAdapter
import com.example.renai.instagram.common.task
import com.example.renai.instagram.common.toUnit
import com.example.renai.instagram.data.FeedPostLike
import com.example.renai.instagram.data.FeedPostsRepository
import com.example.renai.instagram.data.common.map
import com.example.renai.instagram.data.firebase.common.FirebaseLiveData
import com.example.renai.instagram.data.firebase.common.asFeedPost
import com.example.renai.instagram.data.firebase.common.database
import com.example.renai.instagram.data.firebase.common.setValueTrueOrRemove
import com.example.renai.instagram.models.FeedPost
import com.google.android.gms.tasks.Task

class FirebaseFeedPostsRepository : FeedPostsRepository {
    override fun getLikes(postId: String): LiveData<List<FeedPostLike>> =
        FirebaseLiveData(database.child("likes").child(postId)).map {
            it.children.map { FeedPostLike(it.key) }
        }


    override fun toggleLike(postId: String, uid: String): Task<Unit> {
        val reference = database.child("likes")
            .child(postId).child(uid)
        return task { taskSource ->
            reference.addValueEventListener(ValueEventListenerAdapter {
                reference.setValueTrueOrRemove(!it.exists())
            })
        }
    }

    override fun getFeedPosts(uid: String): LiveData<List<FeedPost>> =
        FirebaseLiveData(database.child("feed-posts").child(uid)).map {
            it.children.map { it.asFeedPost()!! }
        }


    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task<Unit> { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap =
                        it.children.map { it.key to it.value }.toMap()

                    database.child("feed-posts").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(
                            TaskSourceOnCompleteListener(
                                taskSource
                            )
                        )
                })
        }

    override fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task<Unit> { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap =
                        it.children.map { it.key to null }.toMap()

                    database.child("feed-posts").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(
                            TaskSourceOnCompleteListener(
                                taskSource
                            )
                        )
                })
        }
}