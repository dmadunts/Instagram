package com.example.renai.instagram.data

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.models.FeedPost
import com.example.renai.instagram.screens.home.FeedPostLikes
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun toggleLike(postId: String, uid: String): Task<Unit>
    fun getLikes(postId: String): LiveData<List<FeedPostLike>>
}

data class FeedPostLike(val userId: String?)
