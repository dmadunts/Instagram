package com.example.renai.instagram.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class FeedPost(
    @get:Exclude val id: String = "",
    val uid: String = "",
    val username: String = "",
    val photo: String? = null,
    val image: String = "",
    val caption: String = "",
    val comments: List<Comment> = emptyList(),
    val timestamp: Any = ServerValue.TIMESTAMP
) {
    fun timestampDate(): Date = Date(timestamp as Long)
}