package com.example.renai.instagram.common.firebase

import com.example.renai.instagram.common.AuthManager
import com.example.renai.instagram.data.firebase.common.auth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class FirebaseAuthManager : AuthManager {
    override fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun signOut() {
        auth.signOut()
    }
}