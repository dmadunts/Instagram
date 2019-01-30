package com.example.renai.instagram.activities.addfriends

import android.arch.lifecycle.LiveData
import com.example.renai.instagram.activities.asUser
import com.example.renai.instagram.activities.map
import com.example.renai.instagram.activities.task
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.TaskSourceOnCompleteListener
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import com.example.renai.instagram.utils.database
import com.example.renai.instagram.utils.liveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth

interface AddFriendsRepository {
    fun getUsers(): LiveData<List<User>>
    fun currentUid(): String?
    fun addFollow(userUid: String, followUid: String): Task<Unit>
    fun removeFollow(userUid: String, followUid: String): Task<Unit>
    fun addFollower(userUid: String, followUid: String): Task<Unit>
    fun removeFollower(userUid: String, followUid: String): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
}

class FirebaseAddFriendsRepository : AddFriendsRepository {

    override fun getUsers(): LiveData<List<User>> =
        database.child("users").liveData().map {
            it.children.map { it.asUser()!! }
        }

    override fun addFollow(userUid: String, followUid: String): Task<Unit> =
        getFollowsRef(userUid, followUid).setValue(true).toUnit()

    override fun removeFollow(userUid: String, followUid: String): Task<Unit> =
        getFollowsRef(userUid, followUid).removeValue().toUnit()

    override fun addFollower(userUid: String, followUid: String): Task<Unit> =
        getFollowersRef(userUid, followUid).setValue(true).toUnit()

    override fun removeFollower(userUid: String, followUid: String): Task<Unit> =
        getFollowersRef(userUid, followUid).removeValue().toUnit()

    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task<Unit> { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap =
                        it.children.map { it.key to it.value }.toMap()

                    database.child("feed-posts").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }


    override fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task<Unit> { taskSource ->
            database.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap =
                        it.children.map { it.key to null }.toMap()

                    database.child("feed-posts").child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }


    private fun getFollowsRef(userUid: String, followUid: String) =
        database.child("users").child(userUid).child("follows").child(followUid)

    private fun getFollowersRef(userUid: String, followUid: String) =
        database.child("users").child(followUid).child("followers").child(userUid)

    override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid
}

fun Task<Void>.toUnit(): Task<Unit> = onSuccessTask { Tasks.forResult(Unit) }

