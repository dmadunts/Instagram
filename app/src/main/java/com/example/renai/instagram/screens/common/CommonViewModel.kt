package com.example.renai.instagram.screens.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener

class CommonViewModel : ViewModel(), OnFailureListener {
    private val _errorMessage = MutableLiveData<String>()
    var errorMessage: LiveData<String> = _errorMessage

    override fun onFailure(e: Exception) {
        setErrorMessage(e.message)
    }

    fun setErrorMessage(message: String?) {
        message?.let { _errorMessage.value = it }
    }
}