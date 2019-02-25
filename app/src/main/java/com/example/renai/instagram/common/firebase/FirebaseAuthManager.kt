package com.example.renai.instagram.common.firebase

import com.example.renai.instagram.common.AuthManager
import com.example.renai.instagram.common.toUnit
import com.example.renai.instagram.data.firebase.common.auth
import com.google.android.gms.tasks.Task

class FirebaseAuthManager : AuthManager {
    override fun signIn(email: String, password: String): Task<Unit> =
        auth.signInWithEmailAndPassword(email, password).toUnit()

    override fun signOut() {
        auth.signOut()
    }
}