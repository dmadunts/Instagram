package com.example.renai.instagram.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class FeedPost(
    val uid: String = "", val username: String = "",
    val photo: String? = null, val image: String = "",
    val commentsCount: Int = 0,
    val caption: String = "", val comments: List<Comment> = emptyList(),
    val timestamp: Any = ServerValue.TIMESTAMP,
    @Exclude val id: String = ""
) {
    fun timestampDate(): Date = Date(timestamp as Long)
}