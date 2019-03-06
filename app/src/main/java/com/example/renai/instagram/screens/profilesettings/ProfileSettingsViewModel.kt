package com.example.renai.instagram.screens.profilesettings

import com.example.renai.instagram.common.AuthManager
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class ProfileSettingsViewModel(private val authManager: AuthManager, onFailureListener: OnFailureListener) :
    BaseViewModel(onFailureListener), AuthManager by authManager

