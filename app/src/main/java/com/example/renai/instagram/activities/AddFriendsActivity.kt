package com.example.renai.instagram.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.ValueEventListenerAdapter

class AddFriendsActivity : AppCompatActivity() {
    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)

        mFirebase = FirebaseHelper(this)
        val uid = mFirebase.auth.currentUser!!.uid
        mFirebase.database.child("users").addValueEventListener(ValueEventListenerAdapter {
            val allUsers = it.children.map { it.getValue(User::class.java)!!.copy(uid = it.key) }
        })
    }
}

class FriendsAdapter : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_friends_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

    }
}
