package com.example.renai.instagram.common.firebase

import com.example.renai.instagram.common.AuthManager
import com.example.renai.instagram.data.firebase.common.auth

class FirebaseAuthManager:AuthManager {
    override fun signOut() {
        auth.signOut()
    }
}