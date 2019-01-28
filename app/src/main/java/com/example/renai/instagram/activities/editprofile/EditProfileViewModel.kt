package com.example.renai.instagram.activities.editprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.renai.instagram.activities.addfriends.FirebaseAddFriendsRepository
import com.example.renai.instagram.models.User

class EditProfileViewModel(private val repository: EditProfileRepository): ViewModel() {
    val user: LiveData<User> = repository.getUser()

}