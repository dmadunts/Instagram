package com.example.renai.instagram.activities.addfriends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.renai.instagram.activities.map
import com.example.renai.instagram.data.FeedPostsRepository
import com.example.renai.instagram.data.UsersRepository
import com.example.renai.instagram.models.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class AddFriendsViewModel(
    private val usersRepository: UsersRepository,
    private val feedPostsRepository: FeedPostsRepository
) : ViewModel() {
    private val currentUid = usersRepository.currentUid()!!

    val usersAndFriends: LiveData<Pair<User, List<User>>> =
        usersRepository.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == currentUid
            }
            userList.first() to otherUsersList
        }

    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return if (follow) {
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
        }
    }
}