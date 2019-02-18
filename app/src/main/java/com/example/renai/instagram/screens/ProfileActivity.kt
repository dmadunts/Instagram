package com.example.renai.instagram.screens

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.renai.instagram.R
import com.example.renai.instagram.common.SimpleCallback
import com.example.renai.instagram.screens.addfriends.AddFriendsActivity
import com.example.renai.instagram.screens.common.*
import com.example.renai.instagram.screens.editprofile.EditProfileActivity
import com.example.renai.instagram.screens.profilesettings.ProfileSettingsActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    companion object {
        const val TAG = "ProfileActivity"
    }

    private lateinit var mAdapter: ImagesAdapter

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
        images_recycler.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter()
        images_recycler.adapter = mAdapter


        setupAuthGuard { uid ->
            val viewModel = initViewModel<ProfileViewModel>()
            viewModel.init(uid)
            viewModel.user.observe(this, Observer {
                it?.let { user ->
                    username_text.text = user.username
                    profile_picture.loadUserPhoto(user.photo)
                }
            })
            viewModel.images.observe(this, Observer {
                it?.let { images ->
                    mAdapter.updateImages(images)
                }
            })
        }
    }

    class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

        class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

        private var images = listOf<String>()
        fun updateImages(newImages: List<String>) {
            val diffResult = DiffUtil.calculateDiff(SimpleCallback(images, newImages, { it }))
            diffResult.dispatchUpdatesTo(this)
        }

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



