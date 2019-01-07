package com.example.renai.instagram.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.renai.instagram.R
import com.example.renai.instagram.utils.CameraHelper
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.GlideApp
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : BaseActivity(2) {
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mFirebase = FirebaseHelper(this)
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()
        back_image.setOnClickListener { finish() }
        share_text.setOnClickListener { share() }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
            } else {
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        if (imageUri != null) {
            mFirebase.storage.child("users").child(mFirebase.uid).child(imageUri.lastPathSegment).putFile(imageUri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        mFirebase.storage.child("users").child(mFirebase.uid).child(imageUri.lastPathSegment)
                            .downloadUrl.addOnSuccessListener { downloadUri ->
                            mFirebase.database.child("images").push().setValue(downloadUri.toString()).addOnCompleteListener{
                                if (it.isSuccessful) {
                                    startActivity(Intent(this, ProfileActivity::class.java))
                                    finish()
                                } else {
                                    showToast(it.exception!!.message!!)
                                }
                            }
                        }.addOnFailureListener { showToast(it.message!!) }
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
        }
    }
}
