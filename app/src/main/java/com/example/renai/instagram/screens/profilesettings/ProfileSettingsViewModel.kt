package com.example.renai.instagram.screens.profilesettings

import android.arch.lifecycle.ViewModel
import com.example.renai.instagram.common.AuthManager

class ProfileSettingsViewModel(private val authManager: AuthManager) : ViewModel(), AuthManager by authManager

