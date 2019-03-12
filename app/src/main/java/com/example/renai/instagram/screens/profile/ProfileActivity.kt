package com.example.renai.instagram.screens.profile

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.example.renai.instagram.R
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
    private lateinit var mViewModel: ProfileViewModel

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
            mViewModel = initViewModel()
            mViewModel.init(uid)

            mViewModel.user.observe(this, Observer {
                it?.let { user ->
                    username_text.text = user.username
                    profile_picture.loadUserPhoto(user.photo)
                    followers_count_text.text = user.followers.size.toString()
                    following_count_text.text = user.follows.size.toString()
                }
            })

            mViewModel.images.observe(this, Observer {
                it?.let { images ->
                    mAdapter.updateImages(images)
                    posts_count_text.text = images.size.toString()
                }
            })
        }
    }
}



