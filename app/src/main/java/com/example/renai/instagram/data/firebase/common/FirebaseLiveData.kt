package com.example.renai.instagram.data.firebase.common

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.common.ValueEventListenerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query

class FirebaseLiveData(private val query: Query) : LiveData<DataSnapshot>() {
    private val listener = ValueEventListenerAdapter {
        value = it
    }

    override fun onActive() {
        super.onActive()
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }
}