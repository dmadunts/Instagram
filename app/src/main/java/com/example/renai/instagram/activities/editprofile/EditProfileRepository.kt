package com.example.renai.instagram.activities.editprofile

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.activities.asUser
import com.example.renai.instagram.activities.map
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.FirebaseLiveData
import com.example.renai.instagram.utils.currentUid
import com.example.renai.instagram.utils.database

interface EditProfileRepository {
    fun getUser(): LiveData<User>

}

class FirebaseEditProfileRepository : EditProfileRepository {
    override fun getUser(): LiveData<User> =
        FirebaseLiveData(database.child("users").child(currentUid()!!)).map {
            it.asUser()!!
        }


}
