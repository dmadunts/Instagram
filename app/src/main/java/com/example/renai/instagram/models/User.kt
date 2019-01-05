package com.example.renai.instagram.models

data class User(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String? = null,
    val website: String? = null,
    val phone: Long? = null,
    val photo: String? = null
)