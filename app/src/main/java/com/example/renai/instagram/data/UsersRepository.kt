package com.example.renai.instagram.data

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.models.User
import com.google.android.gms.tasks.Task


interface UsersRepository {
    fun uploadUserPhoto(localImage: Uri): Task<Uri>
    fun updateUserPhoto(downloadUrl: Uri): Task<Unit>
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>
    fun getUsers(): LiveData<List<User>>
    fun currentUid(): String?
    fun addFollow(userUid: String, followUid: String): Task<Unit>
    fun removeFollow(userUid: String, followUid: String): Task<Unit>
    fun addFollower(userUid: String, followUid: String): Task<Unit>
    fun removeFollower(userUid: String, followUid: String): Task<Unit>
    fun getImages(uid: String): LiveData<List<String>>
    fun isUserExistsForEmail(email: String): Task<Boolean>
    fun createUser(user: User, password: String): Task<Unit>
    fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri>
    fun setUserImage(uid: String, imageDownloadUri: Uri): Task<Unit>
    fun getUser(): LiveData<User>
    fun getUser(uid: String): LiveData<User>
}