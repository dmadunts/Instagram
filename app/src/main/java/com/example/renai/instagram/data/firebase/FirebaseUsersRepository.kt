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
import com.google.firebase.database.DataSnapshot

class FirebaseUsersRepository : UsersRepository {
    override fun setUserImage(uid: String, imageDownloadUri: Uri): Task<Unit> {
        return database.child("images").child(uid)
            .push().setValue(imageDownloadUri.toString()).toUnit()
    }

    //Todo set onFailurelistener
    override fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri> =
        storage.child("users").child(uid).child(imageUri.lastPathSegment!!)
            .putFile(imageUri).onSuccessTask {
                storage.child("users").child(uid).child(imageUri.lastPathSegment!!).downloadUrl
            }

    override fun createUser(user: User, password: String): Task<Unit> =
        auth.createUserWithEmailAndPassword(user.email, password).onSuccessTask {
            database.child("users").child(it!!.user.uid).setValue(user)
        }.toUnit()


    override fun isUserExistsForEmail(email: String): Task<Boolean> =
        auth.fetchSignInMethodsForEmail(email).onSuccessTask {
            val signInMethods = it?.signInMethods ?: emptyList<String>()
            Tasks.forResult(signInMethods.isNotEmpty())
        }


    override fun getImages(uid: String): LiveData<List<String>> {
        return FirebaseLiveData(database.child("images").child(uid)).map {
            it.children.map { it.getValue(String::class.java)!! }.sortedDescending()
        }
    }

    override fun getUsers(): LiveData<List<User>> =
        FirebaseLiveData(database.child("users")).map {
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

    private val storageRef = storage.child("users/${currentUid()}/photo")
    private val databaseRef = database.child("users/${currentUid()}/photo")

    //Todo set onFailurelistener
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

    private fun DataSnapshot.asUser(): User? = getValue(User::class.java)?.copy(uid = key!!)
}
