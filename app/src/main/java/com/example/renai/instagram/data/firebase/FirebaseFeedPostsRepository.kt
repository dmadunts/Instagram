package com.example.renai.instagram.data.firebase

import com.example.renai.instagram.activities.task
import com.example.renai.instagram.data.FeedPostsRepository
import com.example.renai.instagram.utils.TaskSourceOnCompleteListener
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import com.example.renai.instagram.utils.database
import com.example.renai.instagram.utils.toUnit
import com.google.android.gms.tasks.Task

class FirebaseFeedPostsRepository : FeedPostsRepository {

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
}