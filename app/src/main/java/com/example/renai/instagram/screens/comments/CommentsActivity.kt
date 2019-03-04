package com.example.renai.instagram.screens.comments

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.coordinateBtnAndInputs
import com.example.renai.instagram.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : BaseActivity() {
    private lateinit var mAdapter: CommentsAdapter
    private lateinit var mUser: User
    private lateinit var mViewModel: CommentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val postId = intent.getStringExtra(EXTRA_POST_ID) ?: return finish()

        mAdapter = CommentsAdapter()
        comments_recycler.layoutManager = LinearLayoutManager(this)
        comments_recycler.adapter = mAdapter

        coordinateBtnAndInputs(post_comment_btn, comment_text)
        back_image.setOnClickListener { finish() }

        post_comment_btn.setOnClickListener {
            val commentText = comment_text.text.toString()
            if (commentText.isNotEmpty()) {
                mViewModel.createComment(commentText, mUser)
                comment_text.setText("")
            }
        }

        setupAuthGuard {
            mViewModel = initViewModel()
            mViewModel.init(postId)
            mViewModel.comments.observe(this, Observer {
                it?.let {
                    mAdapter.updateComments(it)
                    comments_recycler.smoothScrollToPosition(mViewModel.comments.value!!.size)
                }
            })

            mViewModel.user.observe(this, Observer {
                it?.let {
                    mUser = it
                }
            })

        }
    }

    companion object {
        private const val EXTRA_POST_ID = "POST_ID"
        fun start(context: Context, postId: String) {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(EXTRA_POST_ID, postId)
            context.startActivity(intent)
        }
    }
}