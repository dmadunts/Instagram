package com.example.renai.instagram.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.example.renai.instagram.R
import com.example.renai.instagram.models.User
import com.example.renai.instagram.utils.CameraHelper
import com.example.renai.instagram.utils.FirebaseHelper
import com.example.renai.instagram.utils.GlideApp
import com.example.renai.instagram.utils.ValueEventListenerAdapter
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.activity_share.*
import java.util.*

class ShareActivity : BaseActivity(2) {
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mFirebase = FirebaseHelper(this)
        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()
        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.getValue(User::class.java)!!
        })
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
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
            mFirebase.storage.child("users").child(mFirebase.auth.currentUser!!.uid).child(imageUri.lastPathSegment)
                .putFile(imageUri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        mFirebase.storage.child("users").child(mFirebase.auth.currentUser!!.uid)
                            .child(imageUri.lastPathSegment)
                            .downloadUrl.addOnSuccessListener { imageDownloadUrl ->
                            mFirebase.database.child("images").child(mFirebase.auth.currentUser!!.uid)
                                .push().setValue(imageDownloadUrl.toString()).addOnCompleteListener {
                                    mFirebase.database.child("feed-posts").child(mFirebase.auth.currentUser!!.uid)
                                        .push().setValue(makeFeedPost(imageDownloadUrl)).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                showToast("Post added!")
                                            } else {
                                                showToast(it.exception!!.message!!)
                                            }
                                        }
                                }
                        }.addOnFailureListener { showToast(it.message!!) }
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
        } else {
            showToast("No camera found")
        }
    }

    private fun makeFeedPost(imageDownloadUrl: Uri): FeedPost {
        return FeedPost(
            uid = mFirebase.auth.currentUser!!.uid,
            username = mUser.username,
            image = imageDownloadUrl.toString(),
            photo = mUser.photo,
            caption = caption_input.text.toString()
        )
    }
}

data class FeedPost(
    val uid: String = "", val username: String = "",
    val photo: String? = null, val image: String = "",
    val likesCount: Int = 0, val commentsCount: Int = 0,
    val caption: String = "", val comments: List<Comment> = emptyList(), val timestamp: Any = ServerValue.TIMESTAMP
) {
    fun timestampDate(): Date = Date(timestamp as Long)
}

data class Comment(val uid: String, val username: String, val text: String)

