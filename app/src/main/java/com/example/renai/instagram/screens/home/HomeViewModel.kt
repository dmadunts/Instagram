package com.example.renai.instagram.screens.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.renai.instagram.data.FeedPostsRepository
import com.example.renai.instagram.data.common.map
import com.example.renai.instagram.models.FeedPost
import com.google.android.gms.tasks.OnFailureListener

class HomeViewModel(
    private val onFailureListener: OnFailureListener,
    private val feedPostsRepository: FeedPostsRepository
) : ViewModel() {
    lateinit var uid: String
    lateinit var feedPosts: LiveData<List<FeedPost>>

    fun init(uid: String) {
        this.uid = uid
        feedPosts = feedPostsRepository.getFeedPosts(uid).map {
            it.sortedByDescending { it.timestampDate() }
        }
    }

    fun toggleLike(postId: String) {
        feedPostsRepository.toggleLike(postId, uid).addOnFailureListener(onFailureListener)
    }

    fun getLikes(postId: String): LiveData<FeedPostLikes> {

    }

    fun loadLikes(postId: String): LiveData<FeedPostLikes>{
        feedPostsRepository.getLikes(postId)
        val userLikes = it.children.map { it.key }.toSet()
        val postLikes = FeedPostLikes(
            userLikes.size,
            userLikes.contains(mFirebase.currentUid())
    }
}
