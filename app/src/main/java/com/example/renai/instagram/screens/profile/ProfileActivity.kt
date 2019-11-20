package com.example.renai.instagram.screens.profile

import android.app.Activity
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
    private lateinit var mCameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mCameraHelper = CameraHelper(this)

        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        settings_image.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        add_friends_image.setOnClickListener {
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
            finish()
        }

        website_text.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            val url = "http://" + website_text.text.toString().toLowerCase()
            intent.putExtra("url", url)
            startActivity(intent)
            finish()
        }

        profile_picture.setOnClickListener {
            mCameraHelper.takeCameraPicture()
        }

        //disables recyclerview's scrolling
        images_recycler.layoutManager = object : GridLayoutManager(this, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        mAdapter = ImagesAdapter()
        images_recycler.adapter = mAdapter

        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 4)
            mViewModel = initViewModel()
            mViewModel.init(uid)

            mViewModel.user.observe(this, Observer {
                it?.let { user ->
                    username_title_text.text = user.username
                    profile_picture.loadUserPhoto(user.photo)
                    name_text.text = user.name
                    website_text.text = user.website
                    bio_text.text = user.bio
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EditProfileActivity.REQUEST_CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            mViewModel.uploadAndSetPhoto(mCameraHelper.imageUri!!)
        }
    }
}



