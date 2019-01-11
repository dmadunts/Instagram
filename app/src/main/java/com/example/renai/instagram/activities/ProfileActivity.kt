package com.example.renai.instagram.activities

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
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.GlideApp
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()

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
            mUser = it.getValue(User::class.java)!!
            username_text.text = mUser.username
            profile_picture.loadUserPhoto(mUser.photo)
        })

        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mFirebase.database.child("images").child(mFirebase.auth.currentUser!!.uid)
            .addValueEventListener(ValueEventListenerAdapter {
                val images = it.children.map { it.getValue(String::class.java)!! }
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

        private fun ImageView.loadImage(image: String) {
            GlideApp.with(this).load(image).centerCrop().into(this)
        }
    }
}

class SquareImageView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}



