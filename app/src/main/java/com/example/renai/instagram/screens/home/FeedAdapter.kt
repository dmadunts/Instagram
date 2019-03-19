package com.example.renai.instagram.screens.home

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renai.instagram.R
import com.example.renai.instagram.common.SimpleCallback
import com.example.renai.instagram.common.ValueEventListenerAdapter
import com.example.renai.instagram.data.firebase.common.auth
import com.example.renai.instagram.data.firebase.common.database
import com.example.renai.instagram.models.FeedPost
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.loadImage
import com.example.renai.instagram.screens.common.loadUserPhoto
import com.example.renai.instagram.screens.common.setCaptionText
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedAdapter(private var listener: Listener) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
        fun openComments(postId: String)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var posts = listOf<FeedPost>()
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private val defaultPostLikes = FeedPostLikes(0, false)

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likes = postLikes[position] ?: defaultPostLikes

        database.child("users").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(ValueEventListenerAdapter {
            val user = it.getValue(User::class.java)
            with(holder.view) {
                if (user != null) {
                    user_photo.loadUserPhoto(user.photo)
                    username_title_text.text = user.username
                }
                post_image.loadImage(post.image)
                if (likes.likesCount == 0) {
                    likes_text.visibility = View.GONE
                } else {
                    likes_text.visibility = View.VISIBLE
                    val quantityString =
                        context.resources.getQuantityString(R.plurals.likes_count, likes.likesCount, likes.likesCount)
                    likes_text.text = quantityString
                }
                if (post.caption.isNullOrEmpty()) {
                    caption_text.visibility = View.GONE
                } else {
                    caption_text.visibility = View.VISIBLE
                    if (user != null) {
                        caption_text.setCaptionText(user.username, post.caption)
                    }
                }
                like_image.setOnClickListener { listener.toggleLike(post.id) }
                like_image.setImageResource(
                    if (likes.likedByUser) R.drawable.ic_likes_active
                    else R.drawable.ic_likes_border
                )
                comment_image.setOnClickListener { listener.openComments(post.id) }
                listener.loadLikes(post.id, position)
            }
        })
    }

    fun updatePosts(newPosts: List<FeedPost>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.posts, newPosts, { it.id }))
        this.posts = newPosts
        diffResult.dispatchUpdatesTo(this)
    }
}

