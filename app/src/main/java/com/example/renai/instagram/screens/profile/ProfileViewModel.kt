package com.example.renai.instagram.screens.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository

class ProfileViewModel(private val usersRepository: FirebaseUsersRepository) : ViewModel() {
    val user = usersRepository.getUser()
    lateinit var images: LiveData<List<String>>

    fun init(uid: String) {
        images = usersRepository.getImages(uid)
    }
}
