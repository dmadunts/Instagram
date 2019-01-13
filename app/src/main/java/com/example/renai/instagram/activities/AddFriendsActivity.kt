package com.example.renai.instagram.activities

import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renai.instagram.R
import com.example.renai.instagram.R.id.add_friends_recycler
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_add_friends.*
import kotlinx.android.synthetic.main.add_friends_item.view.*

class AddFriendsActivity : AppCompatActivity(), FriendsAdapter.Listener {


    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User
    private lateinit var mUsers: List<User>
    private lateinit var mAdapter: FriendsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        mFirebase = FirebaseHelper(this)
        mAdapter = FriendsAdapter(this)

        val uid = mFirebase.auth.currentUser!!.uid
        add_friends_recycler.adapter = mAdapter
        add_friends_recycler.layoutManager = LinearLayoutManager(this)

        mFirebase.database.child("users").addValueEventListener(ValueEventListenerAdapter {
            val allUsers = it.children.map { it.getValue(User::class.java)!!.copy(uid = it.key) }
            val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
            mUser = userList.first()
            mUsers = otherUsersList
            mAdapter.update(mUsers, mapOf(mUsers.first().uid!! to true))
        })
    }
    override fun unfollow(uid: String) {

    }
    override fun follow(uid: String) {

    }
}

class FriendsAdapter(private val listener: Listener) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    private var mFollows = mapOf<String, Boolean>()
    private var mUsers = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val user = mUsers[position]
            view.photo_image.loadImage(user.photo)
            view.username_text.text = user.username
            view.name_text.text = user.name
            view.follow_btn.setOnClickListener { listener.follow(user.uid!!) }
            view.following_btn.setOnClickListener { listener.unfollow(user.uid!!) }
            if (mFollows[user.uid] == true) {
                view.follow_btn.visibility = View.GONE
                view.following_btn.visibility = View.VISIBLE
            } else {
                view.follow_btn.visibility = View.VISIBLE
                view.following_btn.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = mUsers.size

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        mUsers = users
        mFollows = follows
    }
}