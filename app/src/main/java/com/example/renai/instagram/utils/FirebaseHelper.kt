package com.example.renai.instagram.utils

import android.app.Activity
import android.net.Uri
import com.example.renai.instagram.activities.showToast
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class FirebaseHelper(private val activity: Activity) {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var storage: StorageReference = FirebaseStorage.getInstance().reference
    var uid: String = auth.currentUser!!.uid

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        auth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUser(updatesMap: Map<String, Any?>, onSuccess: () -> Unit) {
        database.child("users").child(uid).updateChildren(updatesMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
    }

    fun uploadUserPhoto(
        photo: Uri,
        onSuccess: (UploadTask.TaskSnapshot) -> Unit
    ) {
        storage.child("users/$uid/photo").putFile(photo).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result!!)
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUserPhoto(onSuccess: (String) -> Unit) {
        storage.child("users/$uid/photo").downloadUrl.addOnSuccessListener { uri ->
            val photoUrl = uri.toString()
            database.child("users/$uid/photo").setValue(photoUrl).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(photoUrl)
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
        }
    }

    fun currentUserReference(): DatabaseReference = database.child("users").child(uid)

}