package com.example.renai.instagram.screens.comments

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.data.firebase.FirebaseFeedPostsRepository
import com.example.renai.instagram.data.firebase.FirebaseUsersRepository
import com.example.renai.instagram.models.Comment
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener

class CommentsViewModel(
    private val feedPostsRepository: FirebaseFeedPostsRepository,
    onFailureListener: OnFailureListener,
    usersRepository: FirebaseUsersRepository
) : BaseViewModel(onFailureListener) {

    lateinit var comments: LiveData<List<Comment>>
    private lateinit var postId: String
    val user: LiveData<User> = usersRepository.getUser()

    fun init(postId: String) {
        this.postId = postId
        comments = feedPostsRepository.getComments(postId)
    }

    fun createComment(text: String, user: User) {
        val comment = Comment(
            uid = user.uid,
            username = user.username,
            photo = user.photo,
            text = text
        )
        feedPostsRepository.createComment(postId, comment).addOnFailureListener(onFailureListener)
    }
}
