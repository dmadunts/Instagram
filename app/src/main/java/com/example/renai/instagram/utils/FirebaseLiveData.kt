package com.example.renai.instagram.utils

import android.arch.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class FirebaseLiveData(private val reference: DatabaseReference) : LiveData<DataSnapshot>() {
    private val listener = ValueEventListenerAdapter {
        value = it
    }

    override fun onActive() {
        super.onActive()
        reference.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        reference.removeEventListener(listener)
    }
}