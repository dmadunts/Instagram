package com.example.renai.instagram.models

import com.google.firebase.database.Exclude

data class User(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String? = null,
    val website: String? = null,
    val phone: Long? = null,
    val photo: String? = null,
    @Exclude val uid: String = "",
    val follows: Map<String, Boolean> = emptyMap(),
    val followers: Map<String, Boolean> = emptyMap()
)