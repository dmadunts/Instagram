package com.example.renai.instagram.screens.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.renai.instagram.common.AuthManager
import com.example.renai.instagram.common.firebase.FirebaseAuthManager
import com.example.renai.instagram.data.firebase.FirebaseFeedPostsRepository
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.screens.addfriends.AddFriendsViewModel
import com.example.renai.instagram.screens.editprofile.EditProfileViewModel
import com.example.renai.instagram.screens.home.HomeViewModel
import com.example.renai.instagram.screens.profilesettings.ProfileSettingsViewModel
import com.google.android.gms.tasks.OnFailureListener

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val onFailureListener: OnFailureListener) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(AddFriendsViewModel::class.java) ->
                return AddFriendsViewModel(
                    onFailureListener,
                    FirebaseUsersRepository(),
                    FirebaseFeedPostsRepository()
                ) as T
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) ->
                return EditProfileViewModel(onFailureListener, FirebaseUsersRepository()) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                return HomeViewModel(onFailureListener, FirebaseFeedPostsRepository()) as T
            modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java) ->
                return ProfileSettingsViewModel(FirebaseAuthManager()) as T

            else -> error("Unknown viewmodel class: $modelClass")
        }
    }
}