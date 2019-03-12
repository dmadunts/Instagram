package com.example.renai.instagram.screens.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.example.renai.instagram.data.SearchRepository
import com.example.renai.instagram.models.SearchPost
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class SearchViewModel(searchRepository: SearchRepository, onFailureListener: OnFailureListener) :
    BaseViewModel(onFailureListener) {
    private val searchText = MutableLiveData<String>()

    val posts: LiveData<List<SearchPost>> = Transformations.switchMap(searchText) { text ->
        searchRepository.searchPosts(text)
    }

    fun setSearchText(text: String) {
        searchText.value = text
    }

}