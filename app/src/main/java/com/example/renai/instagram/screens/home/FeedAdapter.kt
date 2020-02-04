package com.example.renai.instagram.screens.home

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
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


class FeedAdapter(private val listener: Listener) :
    RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
        fun openComments(postId: String)
        fun showPostContextMenu(view: View)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var posts = listOf<FeedPost>()
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private val defaultPostLikes = FeedPostLikes(0, false)

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes = postLikes + (position to likes)
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
            val currentUser = it.getValue(User::class.java)
            with(holder.view) {
                user_photo.loadUserPhoto(currentUser?.photo)
                username_title_text.text = currentUser?.username
                post_image.loadImage(post.image)
                if (likes.likesCount == 0) {
                    likes_text.visibility = View.GONE
                } else {
                    likes_text.visibility = View.VISIBLE
                    val quantityString =
                        context.resources.getQuantityString(R.plurals.likes_count, likes.likesCount, likes.likesCount)
                    likes_text.text = quantityString
                }

                //TODO On some posts both views are visible
                //Using this allows user to call different context menus
                // depending on who is creator of the post
                if (post.uid == auth.currentUser!!.uid) {
                    other_user_more.visibility = View.GONE
                    current_user_more.visibility = View.VISIBLE
                    current_user_more.setOnClickListener { listener.showPostContextMenu(current_user_more) }
                } else {
                    other_user_more.visibility = View.GONE
                    other_user_more.visibility = View.VISIBLE
                    other_user_more.setOnClickListener { listener.showPostContextMenu(other_user_more) }
                }

                if (post.caption.isEmpty()) {
                    caption_text.visibility = View.GONE
                } else {
                    caption_text.visibility = View.VISIBLE
                    if (currentUser != null) {
                        caption_text.setCaptionText(currentUser.username, post.caption)
                    }
                }

                like_image.setImageResource(
                    if (likes.likedByUser) R.drawable.ic_likes_active
                    else R.drawable.ic_like
                )

                like_image.setOnClickListener {
                    listener.toggleLike(post.id)
                    animateSmallHeart(like_image)
                }

                //DoubleTapListener for Instagram-style like
                post_image.setOnTouchListener(object : View.OnTouchListener {
                    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                        override fun onDoubleTap(e: MotionEvent?): Boolean {
                            animateBigHeart(big_like)
                            if (likes.likedByUser) {
                                animateWhenLiked(like_image)
                            } else {
                                listener.toggleLike(post.id)
                                animateSmallHeart(like_image)
                            }
                            return super.onDoubleTap(e)
                        }
                    })

                    @SuppressLint("ClickableViewAccessibility")
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        gestureDetector.onTouchEvent(event)
                        return true
                    }
                })
                comment_image.setOnClickListener { listener.openComments(post.id) }
                listener.loadLikes(post.id, position)
            }
        })
    }

    //TODO Need's refactor for proper many clicks handling
    private fun animateBigHeart(view: ImageView) {
        val animatorSet = AnimatorInflater.loadAnimator(view.context, R.animator.big_heart_animator) as AnimatorSet
        animatorSet.setTarget(view)
        animatorSet.start()
    }

    //TODO Need's refactor for proper many clicks handling
    private fun animateSmallHeart(view: ImageView) {
        val animatorSet = AnimatorInflater.loadAnimator(view.context, R.animator.small_heart_animator) as AnimatorSet
        animatorSet.setTarget(view)
        animatorSet.start()
    }

    //This animation is used when post is already liked by user.
    private fun animateWhenLiked(view: ImageView) {
        val animatorSet = AnimatorInflater
            .loadAnimator(view.context, R.animator.small_heart_when_liked_animation)
                as AnimatorSet
        animatorSet.setTarget(view)
        animatorSet.start()
    }

    fun updatePosts(newPosts: List<FeedPost>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.posts, newPosts, { it.id }))
        this.posts = newPosts
        diffResult.dispatchUpdatesTo(this)
    }
}


