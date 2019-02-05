package com.example.renai.instagram.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.renai.instagram.R
import com.example.renai.instagram.common.ValueEventListenerAdapter
import com.example.renai.instagram.data.firebase.common.FirebaseHelper
import com.example.renai.instagram.data.firebase.common.asUser
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.addfriends.AddFriendsActivity
import com.example.renai.instagram.screens.common.BaseActivity
import com.example.renai.instagram.screens.common.loadImage
import com.example.renai.instagram.screens.common.loadUserPhoto
import com.example.renai.instagram.screens.common.setupBottomNavigation
import com.example.renai.instagram.screens.editprofile.EditProfileActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    companion object {
        const val TAG = "ProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation(4)

        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        settings_image.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }
        add_friends_image.setOnClickListener {
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }

        mFirebase = FirebaseHelper(this)
        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.asUser()!!
            username_text.text = mUser.username
            profile_picture.loadUserPhoto(mUser.photo)
        })

        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mFirebase.database.child("images").child(mFirebase.currentUid()!!)
            .addValueEventListener(ValueEventListenerAdapter {
                val images = it.children.map { it.getValue(String::class.java)!! }
                    .sortedDescending()
                images_recycler.adapter = ImagesAdapter(images)
            })
    }

    class ImagesAdapter(private val images: List<String>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

        class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val image = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false) as ImageView
            return ViewHolder(image)
        }

        override fun getItemCount(): Int = images.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.image.loadImage(images[position])
        }
    }
}

class SquareImageView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}



