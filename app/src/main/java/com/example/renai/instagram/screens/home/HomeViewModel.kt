package com.example.renai.instagram.screens.home

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.common.SingleLiveEvent
import com.example.renai.instagram.data.FeedPostsRepository
import com.example.renai.instagram.data.common.map
import com.example.renai.instagram.models.FeedPost
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class HomeViewModel(
    onFailureListener: OnFailureListener,
    private val feedPostsRepository: FeedPostsRepository
) : BaseViewModel(onFailureListener) {
    lateinit var uid: String
    lateinit var feedPosts: LiveData<List<FeedPost>>
    private var loadedLikes = mapOf<String, LiveData<FeedPostLikes>>()
    private val _goToCommentsScreen = SingleLiveEvent<String>()
    val goToCommentsScreen = _goToCommentsScreen

    fun init(uid: String) {
        this.uid = uid
        feedPosts = feedPostsRepository.getFeedPosts(uid).map {
            it.sortedByDescending { it.timestampDate() }
        }
    }


    fun toggleLike(postId: String) {
        feedPostsRepository.toggleLike(postId, uid).addOnFailureListener(onFailureListener)
    }

    fun getLikes(postId: String): LiveData<FeedPostLikes>? = loadedLikes[postId]

    fun loadLikes(postId: String): LiveData<FeedPostLikes> {
        val existingLoadedLikes = loadedLikes[postId]
        if (existingLoadedLikes == null) {
            val liveData = feedPostsRepository.getLikes(postId).map { likes ->
                FeedPostLikes(
                    likesCount = likes.size,
                    likedByUser = likes.find { it.userId == uid } != null)
            }
            loadedLikes = loadedLikes + (postId to liveData)
            return liveData
        } else {
            return existingLoadedLikes
        }
    }

    fun openComments(postId: String) {
        _goToCommentsScreen.value = postId
    }
}
