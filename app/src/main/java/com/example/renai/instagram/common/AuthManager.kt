package com.example.renai.instagram.common

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface AuthManager{
    fun signOut()
    fun signIn(email: String, password: String): Task<Unit>
}