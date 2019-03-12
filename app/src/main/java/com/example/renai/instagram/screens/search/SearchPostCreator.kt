package com.example.renai.instagram.screens.search

import android.arch.lifecycle.Observer
import android.util.Log
import com.example.renai.instagram.common.BaseEventListener
import com.example.renai.instagram.common.Event
import com.example.renai.instagram.common.EventBus
import com.example.renai.instagram.data.SearchRepository
import com.example.renai.instagram.models.SearchPost

class SearchPostCreator(searchRepository: SearchRepository) : BaseEventListener() {
    companion object {
        const val TAG = "SearchPostCreator"
    }

    init {
        EventBus.events.observe(this, Observer {
            it?.let { event ->
                when (event) {
                    is Event.CreateFeedPost -> {
                        val searchPost = with(event.feedPost) {
                            SearchPost(
                                image = image,
                                caption = caption,
                                postId = id
                            )
                        }
                        searchRepository.createPost(searchPost).addOnFailureListener { exception ->
                            Log.d(TAG, "Failed to create search post for event: $event", exception)
                        }
                    }
                    else -> {
                    }
                }
            }
        })
    }
}