package com.example.renai.instagram.data.firebase

import android.arch.lifecycle.LiveData
import android.net.Uri
import com.example.renai.instagram.common.toUnit
import com.example.renai.instagram.data.UsersRepository
import com.example.renai.instagram.data.common.map
import com.example.renai.instagram.data.firebase.common.*
import com.example.renai.instagram.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class FirebaseUsersRepository : UsersRepository {
    override fun getUsers(): LiveData<List<User>> =
        database.child("users").liveData().map {
            it.children.map { it.asUser()!! }
        }

    override fun addFollow(userUid: String, followUid: String): Task<Unit> =
        getFollowsRef(userUid, followUid).setValue(true).toUnit()

    override fun removeFollow(userUid: String, followUid: String): Task<Unit> =
        getFollowsRef(userUid, followUid).removeValue().toUnit()

    override fun addFollower(userUid: String, followUid: String): Task<Unit> =
        getFollowersRef(userUid, followUid).setValue(true).toUnit()

    override fun removeFollower(userUid: String, followUid: String): Task<Unit> =
        getFollowersRef(userUid, followUid).removeValue().toUnit()

    override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid
    private fun getFollowsRef(userUid: String, followUid: String) =
        database.child("users").child(userUid).child("follows").child(followUid)

    private fun getFollowersRef(userUid: String, followUid: String) =
        database.child("users").child(followUid).child("followers").child(userUid)

    override fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != currentUser.username) updatesMap["username"] = newUser.username
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio
        if (newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone
        if (newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if (newUser.website != currentUser.website) updatesMap["website"] = newUser.website
        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
    }

    override fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentEmail, password)
            currentUser.reauthenticate(credential).onSuccessTask {
                currentUser.updateEmail(newEmail)
            }.toUnit()
        } else {
            Tasks.forException(IllegalStateException("User is not authenticated"))
        }
    }

    private val storageRef = storage.child("users/${currentUid()!!}/photo")
    private val databaseRef = database.child("users/${currentUid()!!}/photo")

    override fun uploadUserPhoto(localImage: Uri): Task<Uri> =
        storageRef.putFile(localImage).onSuccessTask {
            storageRef.downloadUrl
                .addOnCompleteListener {}
                .addOnFailureListener {}
        }

    override fun updateUserPhoto(downloadUrl: Uri): Task<Unit> =
        databaseRef.setValue(downloadUrl.toString()).toUnit()

    override fun getUser(): LiveData<User> =
        FirebaseLiveData(database.child("users").child(currentUid()!!)).map {
            it.asUser()!!
        }
}
