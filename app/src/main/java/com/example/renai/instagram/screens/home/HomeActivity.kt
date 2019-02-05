package com.example.renai.instagram.screens.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.renai.instagram.R
import com.example.renai.instagram.common.ValueEventListenerAdapter
import com.example.renai.instagram.data.firebase.common.FirebaseHelper
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.setupAuthGuard
import com.example.renai.instagram.screens.common.setupBottomNavigation
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), FeedAdapter.Listener {
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter
    private var mLikesListener: Map<String, ValueEventListener> = emptyMap()
    private lateinit var mViewModel: HomeViewModel

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(0)

        setupAuthGuard { uid ->
            mViewModel = initViewModel()
            mViewModel.init(uid)
            mViewModel.feedPosts.observe(this, Observer {
                it?.let {
                    val posts = it
                    mAdapter = FeedAdapter(this, posts)
                    feed_recycler.adapter = mAdapter
                    feed_recycler.layoutManager = LinearLayoutManager(this)
                }
            })

        }
    }

    override fun toggleLike(postId: String) {
        mViewModel.toggleLike(postId)
    }

    override fun loadLikes(postId: String, position: Int) {
        if (mViewModel.getLikes(postId) == null) {
            mViewModel.loadLikes(postId).observe(this, Observer {
                it?.let {
                    val userLikes = it.children.map { it.key }.toSet()
                    val postLikes = FeedPostLikes(
                        userLikes.size,
                        userLikes.contains(mFirebase.currentUid())
                    )
                    mAdapter.updatePostLikes(position, postLikes)
                }
            })
        }

        fun createListener() =
            mFirebase.database.child("likes").child(postId).addValueEventListener(
                ValueEventListenerAdapter {
                    val userLikes = it.children.map { it.key }.toSet()
                    val postLikes = FeedPostLikes(
                        userLikes.size,
                        userLikes.contains(mFirebase.currentUid())
                    )
                    mAdapter.updatePostLikes(position, postLikes)
                })
        if (mLikesListener[postId] == null) {
            mLikesListener += (postId to createListener())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mLikesListener.values.forEach { mFirebase.database.removeEventListener(it) }
    }
}


