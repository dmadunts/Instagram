package com.example.renai.instagram.activities.addfriends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.renai.instagram.activities.map
import com.example.renai.instagram.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class AddFriendsViewModel(private val repository: AddFriendsRepository) : ViewModel() {
    private val currentUid = repository.currentUid()!!

    val usersAndFriends: LiveData<Pair<User, List<User>>> =
        repository.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == currentUid
            }
            userList.first() to otherUsersList
        }

    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return if (follow) {
            Tasks.whenAll(
                repository.addFollow(currentUid, uid),
                repository.addFollower(currentUid, uid),
                repository.copyFeedPosts(postsAuthorUid = uid, uid = currentUid)
            )
        } else {
            Tasks.whenAll(
                repository.removeFollow(currentUid, uid),
                repository.removeFollower(currentUid, uid),
                repository.removeFeedPosts(postsAuthorUid = uid, uid = currentUid)
            )
        }
    }
}