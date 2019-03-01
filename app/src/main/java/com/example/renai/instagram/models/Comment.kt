package com.example.renai.instagram.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.sql.Date

data class Comment(
    val uid: String = "",
    val username: String = "",
    val text: String = "",
    val photo: String? = null,
    val timestamp: Any = ServerValue.TIMESTAMP,
    @get:Exclude val id: String = ""
) {
    fun timestampDate() = Date(timestamp as Long)
}