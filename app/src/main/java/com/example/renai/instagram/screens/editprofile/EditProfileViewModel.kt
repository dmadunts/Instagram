package com.example.renai.instagram.screens.editprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.example.renai.instagram.data.UsersRepository
import com.example.renai.instagram.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task

class EditProfileViewModel(
    private val onFailureListener: OnFailureListener,
    private val usersRepository: UsersRepository
) : ViewModel() {
    val user: LiveData<User> = usersRepository.getUser()

    fun uploadAndSetPhoto(localImage: Uri): Task<Unit> =
        usersRepository.uploadUserPhoto(localImage).onSuccessTask { downloadUrl ->
            usersRepository.updateUserPhoto(downloadUrl!!)
        }.addOnFailureListener(onFailureListener)

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        usersRepository.updateEmail(currentEmail = currentEmail, newEmail = newEmail, password = password)
            .addOnFailureListener(onFailureListener)

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
        usersRepository.updateUserProfile(currentUser = currentUser, newUser = newUser)
            .addOnFailureListener(onFailureListener)
}

