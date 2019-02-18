package com.example.renai.instagram.screens.common

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.renai.instagram.common.firebase.FirebaseAuthManager
import com.example.renai.instagram.data.firebase.FirebaseFeedPostsRepository
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.screens.LoginViewModel
import com.example.renai.instagram.screens.addfriends.AddFriendsViewModel
import com.example.renai.instagram.screens.editprofile.EditProfileViewModel
import com.example.renai.instagram.screens.home.HomeViewModel
import com.example.renai.instagram.screens.profilesettings.ProfileSettingsViewModel
import com.google.android.gms.tasks.OnFailureListener

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val app: Application,
    private val commonViewModel: CommonViewModel,
    private val onFailureListener: OnFailureListener
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val usersRepository by lazy { FirebaseUsersRepository() }
        val feedPostsRepository by lazy { FirebaseFeedPostsRepository() }
        val authManager by lazy { FirebaseAuthManager() }
        when {
            modelClass.isAssignableFrom(AddFriendsViewModel::class.java) ->
                return AddFriendsViewModel(
                    onFailureListener,
                    usersRepository,
                    feedPostsRepository
                ) as T
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) ->
                return EditProfileViewModel(onFailureListener, usersRepository) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                return HomeViewModel(onFailureListener, feedPostsRepository) as T
            modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java) ->
                return ProfileSettingsViewModel(authManager) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                return LoginViewModel(authManager, app, commonViewModel, onFailureListener) as T

            else -> error("Unknown viewmodel class: $modelClass")
        }
    }
}