package com.example.renai.instagram.activities

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.renai.instagram.activities.addfriends.AddFriendsViewModel
import com.example.renai.instagram.activities.editprofile.EditProfileViewModel
import com.example.renai.instagram.data.firebase.FirebaseFeedPostsRepository
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(FirebaseUsersRepository(), FirebaseFeedPostsRepository()) as T

        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(FirebaseUsersRepository()) as T

        } else {
            error("Unknown viewmodel class: $modelClass")
        }
    }
}