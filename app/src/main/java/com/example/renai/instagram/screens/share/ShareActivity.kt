package com.example.renai.instagram.screens.share

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.*
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity() {
    private lateinit var mViewModel: ShareViewModel
    private lateinit var mCamera: CameraHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupAuthGuard {
            mViewModel = initViewModel()

            mCamera = CameraHelper(this)
            mCamera.takeCameraPicture()

            mViewModel.user.observe(this, Observer {
                it?.let { user ->
                    mUser = user
                }
            })
            back_image.setOnClickListener { finish() }
            share_text.setOnClickListener { share() }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                post_image.loadImage(mCamera.imageUri?.toString())
            } else {
                finish()
            }
        }
    }

    private fun share() {
        mViewModel.share(mUser, caption_input.text.toString(), mCamera.imageUri)
    }
}

