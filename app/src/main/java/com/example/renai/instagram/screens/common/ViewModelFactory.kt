package com.example.renai.instagram.screens.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.renai.instagram.screens.InstagramApp
import com.example.renai.instagram.screens.addfriends.AddFriendsViewModel
import com.example.renai.instagram.screens.comments.CommentsViewModel
import com.example.renai.instagram.screens.editprofile.EditProfileViewModel
import com.example.renai.instagram.screens.home.HomeViewModel
import com.example.renai.instagram.screens.login.LoginViewModel
import com.example.renai.instagram.screens.profile.ProfileViewModel
import com.example.renai.instagram.screens.profilesettings.ProfileSettingsViewModel
import com.example.renai.instagram.screens.register.RegisterViewModel
import com.example.renai.instagram.screens.share.ShareViewModel
import com.google.android.gms.tasks.OnFailureListener

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val app: InstagramApp,
    private val commonViewModel: CommonViewModel,
    private val onFailureListener: OnFailureListener
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val usersRepository = app.usersRepository
        val feedPostsRepository = app.feedPostsRepository
        val authManager = app.authManager

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
                return ProfileSettingsViewModel(authManager, onFailureListener) as T

            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                return LoginViewModel(
                    authManager,
                    app,
                    commonViewModel,
                    onFailureListener
                ) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                return RegisterViewModel(
                    usersRepository, commonViewModel,
                    app, onFailureListener
                ) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                return ProfileViewModel(usersRepository, onFailureListener) as T

            modelClass.isAssignableFrom(ShareViewModel::class.java) ->
                return ShareViewModel(usersRepository, feedPostsRepository, onFailureListener) as T

            modelClass.isAssignableFrom(CommentsViewModel::class.java) ->
                return CommentsViewModel(feedPostsRepository, onFailureListener, usersRepository) as T

            else -> error("Unknown viewmodel class: $modelClass")
        }
    }
}