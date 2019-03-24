package com.example.renai.instagram.screens.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.example.renai.instagram.R
import com.example.renai.instagram.screens.comments.CommentsActivity
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.setupAuthGuard
import com.example.renai.instagram.screens.common.setupBottomNavigation
import com.example.renai.instagram.screens.common.showToast
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : BaseActivity(), FeedAdapter.Listener {

    override fun showPostContextMenu(view: View) {
        registerForContextMenu(view)
        view.showContextMenu()
    }

    private lateinit var mAdapter: FeedAdapter
    private lateinit var mViewModel: HomeViewModel

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = MenuInflater(this)
        when (v?.id){
            R.id.current_user_more -> inflater.inflate(R.menu.user_feed_context_menu, menu)
            R.id.other_user_more -> inflater.inflate(R.menu.other_feed_context_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.edit -> {
                showToast("Edit clicked!")
            }
            R.id.share -> {
                showToast("Share clicked!")
            }
            R.id.delete -> {
                showToast("Delete clicked!")
            }
            R.id.archive -> {
                showToast("Archive clicked!")
            }
        }
        return super.onContextItemSelected(item)
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
                }
            })

            mViewModel.goToCommentsScreen.observe(this, Observer {
                it?.let { postId ->
                    CommentsActivity.start(this, postId)
                }
            })

            mViewModel.user.observe(this, Observer {
                it?.let {
                    mAdapter.notifyDataSetChanged()
                }
            })

            mAdapter = FeedAdapter(this)
            feed_recycler.adapter = mAdapter
            feed_recycler.layoutManager = LinearLayoutManager(this)

            //Removes blinking in RecyclerView
            val animator = feed_recycler.itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
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


