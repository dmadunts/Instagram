package com.example.renai.instagram.data

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.models.Comment
import com.example.renai.instagram.models.FeedPost
import com.google.android.gms.tasks.Task

interface FeedPostsRepository {
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun getFeedPosts(uid: String): LiveData<List<FeedPost>>
    fun toggleLike(postId: String, uid: String): Task<Unit>
    fun getLikes(postId: String): LiveData<List<FeedPostLike>>
    fun getComments(postId: String): LiveData<List<Comment>>
    fun createComment(postId: String, comment: Comment): Task<Unit>
}

data class FeedPostLike(val userId: String?)
