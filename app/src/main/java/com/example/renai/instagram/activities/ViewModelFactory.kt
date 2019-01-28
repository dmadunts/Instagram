package com.example.renai.instagram.activities

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.renai.instagram.activities.addfriends.AddFriendsViewModel
import com.example.renai.instagram.activities.addfriends.FirebaseAddFriendsRepository
import com.example.renai.instagram.activities.editprofile.EditProfileViewModel
import com.example.renai.instagram.activities.editprofile.FirebaseEditProfileRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(FirebaseAddFriendsRepository()) as T

        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(FirebaseEditProfileRepository()) as T

        } else {
            error("Unknown viewmodel class: $modelClass")
        }
    }
}