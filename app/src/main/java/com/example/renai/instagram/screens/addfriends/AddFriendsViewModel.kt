package com.example.renai.instagram.screens.addfriends

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.data.FeedPostsRepository
import com.example.renai.instagram.data.UsersRepository
import com.example.renai.instagram.data.common.map
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.BaseViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class AddFriendsViewModel(
    onFailureListener: OnFailureListener,
    private val usersRepository: UsersRepository,
    private val feedPostsRepository: FeedPostsRepository
) : BaseViewModel(onFailureListener) {
    private val currentUid = usersRepository.currentUid()!!

    val usersAndFriends: LiveData<Pair<User, List<User>>> =
        usersRepository.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == currentUid
            }
            userList.first() to otherUsersList
        }

    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return (if (follow) {
            Tasks.whenAll(
                usersRepository.addFollow(currentUid, uid),
                usersRepository.addFollower(currentUid, uid),
                feedPostsRepository.copyFeedPosts(postsAuthorUid = uid, uid = currentUid)
            )
        } else {
            Tasks.whenAll(
                usersRepository.removeFollow(currentUid, uid),
                usersRepository.removeFollower(currentUid, uid),
                feedPostsRepository.removeFeedPosts(postsAuthorUid = uid, uid = currentUid)
            )
        }).addOnFailureListener(onFailureListener)
    }
}