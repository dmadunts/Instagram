package com.example.renai.instagram.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.renai.instagram.R
import com.example.renai.instagram.models.FeedPost
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_item.view.*

class HomeActivity : BaseActivity(0), FeedAdapter.Listener {
    private val TAG = "HomeActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        mFirebase = FirebaseHelper(this)

        mFirebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.asUser()!!
        })
    }


    override fun onStart() {
        super.onStart()
        val loginUser = mFirebase.auth.currentUser
        if (loginUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mFirebase.database.child("feed-posts").child(loginUser.uid)
                .addValueEventListener(ValueEventListenerAdapter {
                    val posts = it.children.map { it.asFeedPost()!! }
                        .sortedByDescending { it.timestampDate() }

                    mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
                        mAdapter = FeedAdapter(this, posts)
                        feed_recycler.adapter = mAdapter
                        feed_recycler.layoutManager = LinearLayoutManager(this)
                    })
                })
        }
    }

    override fun toggleLike(postId: String) {
        val reference = mFirebase.database.child("likes").child(postId).child(mFirebase.currentUid()!!)
        reference
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                reference.setValueTrueOrRemove(!it.exists())
            })
    }

    override fun loadLikes(postId: String, position: Int) {
        mFirebase.database.child("likes").child(postId).addListenerForSingleValueEvent(
            ValueEventListenerAdapter {
                val userLikes = it.children.map { it.key }.toSet()
                val postLikes = FeedPostLikes(userLikes.size, userLikes.contains(mFirebase.currentUid()))
                mAdapter.updatePostLikes(position, postLikes)
            })
    }
}

data class FeedPostLikes(val likesCount: Int, val likes: Boolean) {

}

class FeedAdapter(private var listener: Listener, private val posts: List<FeedPost>) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes + (position to likes)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likes = postLikes[position]
        with(holder.view) {
            user_photo_image.loadUserPhoto(post.photo)
            username_text.text = post.username
            post_image.loadImage(post.image)
            if (likes!!.likesCount == 0) {
                likes_text.visibility = View.GONE
            } else {
                likes_text.visibility = View.VISIBLE
                likes_text.text = "${likes!!.likesCount} likes"
            }
            if (post.caption.isEmpty()) {
                caption_text.visibility = View.GONE
            } else {
                caption_text.visibility = View.VISIBLE
                caption_text.setCaptionText(post.username, post.caption)
            }
            like_image.setOnClickListener { listener.toggleLike(post.id) }
            listener.loadLikes(post.id, position)
        }
    }

    private fun TextView.setCaptionText(username: String, caption: String) {
        val usernameSpannable = SpannableString(username)
        usernameSpannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, usernameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        usernameSpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.context.showToast("Username is clicked")
            }

            override fun updateDrawState(ds: TextPaint) {}
        }, 0, usernameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        caption_text.text = SpannableStringBuilder().append(usernameSpannable).append(" ").append(caption)
        caption_text.movementMethod = LinkMovementMethod.getInstance()
    }
}