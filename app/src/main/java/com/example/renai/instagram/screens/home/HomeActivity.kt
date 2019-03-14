package com.example.renai.instagram.screens.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.comments.CommentsActivity
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.setupAuthGuard
import com.example.renai.instagram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), FeedAdapter.Listener {
    private lateinit var mAdapter: FeedAdapter
    private lateinit var mViewModel: HomeViewModel

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 0)
            mViewModel = initViewModel()
            mViewModel.init(uid)

            mViewModel.feedPosts.observe(this, Observer {
                it?.let {
                    mAdapter.updatePosts(it)
//                    feed_recycler.smoothScrollToPosition(0) TODO scroll when size changes
                }
            })

            mViewModel.goToCommentsScreen.observe(this, Observer {
                it?.let { postId ->
                    CommentsActivity.start(this, postId)
                }
            })

//            mViewModel.user.observe(this, Observer {
//                it?.let {
//                    mAdapter.editPosts(it)
//                }
//            })

            mAdapter = FeedAdapter(this)
            feed_recycler.adapter = mAdapter
            feed_recycler.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun toggleLike(postId: String) {
        mViewModel.toggleLike(postId)
    }

    override fun loadLikes(postId: String, position: Int) {
        if (mViewModel.getLikes(postId) == null) {
            mViewModel.loadLikes(postId).observe(this, Observer {
                it?.let { postLikes ->
                    mAdapter.updatePostLikes(position, postLikes)
                }
            })
        }
    }

    override fun openComments(postId: String) {
        mViewModel.openComments(postId)
    }
}


