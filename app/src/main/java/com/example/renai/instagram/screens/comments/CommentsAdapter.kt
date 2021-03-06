package com.example.renai.instagram.screens.comments

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renai.instagram.R
import com.example.renai.instagram.common.SimpleCallback
import com.example.renai.instagram.common.ValueEventListenerAdapter
import com.example.renai.instagram.common.formatRelativeTimestamp
import com.example.renai.instagram.data.firebase.common.auth
import com.example.renai.instagram.data.firebase.common.database
import com.example.renai.instagram.models.Comment
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.loadUserPhoto
import com.example.renai.instagram.screens.common.setCaptionText
import kotlinx.android.synthetic.main.comments_item.view.*
import java.util.*

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private var comments = listOf<Comment>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comments_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        database.child("users").child(auth.currentUser!!.uid).addValueEventListener(ValueEventListenerAdapter {
            val currentUser = it.getValue(User::class.java)!!
            with(holder.view) {
                photo.loadUserPhoto(currentUser.photo)
                text.setCaptionText(currentUser.username, comment.text)
                date.text = formatRelativeTimestamp(comment.timestampDate(), Date())
            }
        })
    }

    fun updateComments(newComments: List<Comment>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(comments, newComments) { it.id })
        this.comments = newComments
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = comments.size

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}