package com.example.renai.instagram.models

data class User(
    val name: String = "",
    val username: String = "",
    val bio: String = "",
    val website: String = "",
    val phone: Long = 0L,
    val email: String = "",
    val photo: String = ""
)