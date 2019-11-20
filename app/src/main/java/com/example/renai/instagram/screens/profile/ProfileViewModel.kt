package com.example.renai.instagram.screens.profile

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task

class ProfileViewModel(private val usersRepository: FirebaseUsersRepository, onFailureListener: OnFailureListener) :
    BaseViewModel(onFailureListener) {
    var user = usersRepository.getUser()
    lateinit var images: LiveData<List<String>>

    fun init(uid: String) {
        if (!this::images.isInitialized) {
            images = usersRepository.getImages(uid)
        }
    }

    fun uploadAndSetPhoto(localImage: Uri): Task<Unit> =
        usersRepository.uploadUserPhoto(localImage).onSuccessTask { downloadUrl ->
            usersRepository.updateUserPhoto(downloadUrl!!)
        }.addOnFailureListener(onFailureListener)

}
