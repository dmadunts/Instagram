package com.example.renai.instagram.screens.profile

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class ProfileViewModel(private val usersRepository: FirebaseUsersRepository, onFailureListener: OnFailureListener) :
    BaseViewModel(onFailureListener) {
    val user = usersRepository.getUser()
    lateinit var images: LiveData<List<String>>

    fun init(uid: String) {
        if (!this::images.isInitialized) {
            images = usersRepository.getImages(uid)
        }
    }
}
