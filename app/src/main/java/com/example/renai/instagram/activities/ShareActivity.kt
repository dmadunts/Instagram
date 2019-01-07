package com.example.renai.instagram.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.renai.instagram.R
import com.example.renai.instagram.utils.CameraHelper
import com.example.renai.instagram.utils.GlideApp
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mCameraHelper = CameraHelper(this)
        mCameraHelper.takeCameraPicture()
        back_image.setOnClickListener { finish() }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCameraHelper.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            GlideApp.with(this).load(mCameraHelper.imageUri).centerCrop().into(post_image)
        }
    }
}
