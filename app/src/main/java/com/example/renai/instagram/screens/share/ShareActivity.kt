package com.example.renai.instagram.screens.share

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.screens.common.*
import com.example.renai.instagram.screens.home.HomeActivity
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity() {
    private lateinit var mViewModel: ShareViewModel
    private lateinit var mCamera: CameraHelper
    private lateinit var mUser: User
    private val dialog = LoadingDialog()

    companion object {
        const val REQUEST_CAMERA_CODE = 13
    }


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

            mViewModel.shareStartedEvent.observe(this, Observer {
                dialog.show(supportFragmentManager, "Loading dialog")
            })

            mViewModel.shareComletedEvent.observe(this, Observer {
                dialog.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            })
        }
        back_image.setOnClickListener { finish() }
        share_text.setOnClickListener { share() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAMERA_CODE) {
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

