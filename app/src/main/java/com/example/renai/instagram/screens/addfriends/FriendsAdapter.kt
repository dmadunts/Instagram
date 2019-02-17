package com.example.renai.instagram.screens.addfriends

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renai.instagram.R
import com.example.renai.instagram.common.SimpleCallback
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.loadUserPhoto
import kotlinx.android.synthetic.main.add_friends_item.view.*


class FriendsAdapter(private val listener: Listener) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    private var mPositions = mapOf<String, Int>()
    private var mFollows = mapOf<String, Boolean>()
    private var mUsers = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.view) {
            val user = mUsers[position]
            photo_image.loadUserPhoto(user.photo)
            username_text.text = user.username
            name_text.text = user.name
            follow_btn.setOnClickListener { listener.follow(user.uid) }
            following_btn.setOnClickListener { listener.unfollow(user.uid) }
            if (mFollows[user.uid] == true) {
                follow_btn.visibility = View.GONE
                following_btn.visibility = View.VISIBLE
            } else {
                follow_btn.visibility = View.VISIBLE
                following_btn.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = mUsers.size

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        val diffResults = DiffUtil.calculateDiff(SimpleCallback(mUsers, users, {it.uid}))
        mUsers = users
        mPositions = users.withIndex().map { (inx, user) -> user.uid to inx }.toMap()
        mFollows = follows
        diffResults.dispatchUpdatesTo(this)
    }

    fun followed(uid: String) {
        mFollows = mFollows + (uid to true)
        notifyItemChanged(mPositions[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows = mFollows - uid
        notifyItemChanged(mPositions[uid]!!)
    }
}