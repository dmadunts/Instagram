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
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance()
        .reference
    private val mStorage: StorageReference = FirebaseStorage.getInstance()
        .reference

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        mAuth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        mAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUser(updatesMap: Map<String, Any?>, onSuccess: () -> Unit) {
        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
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
        mStorage.child("users/${mAuth.currentUser!!.uid}/photo").putFile(photo).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result!!)
            } else {
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUserPhoto(onSuccess: (String) -> Unit) {
        mStorage.child("users/${mAuth.currentUser!!.uid}/photo").downloadUrl.addOnSuccessListener { uri ->
            val photoUrl = uri.toString()
            mDatabase.child("users/${mAuth.currentUser!!.uid}/photo").setValue(photoUrl).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(photoUrl)
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
        }
    }

    fun currentUserReference(): DatabaseReference = mDatabase.child("users").child(mAuth.currentUser!!.uid)

}