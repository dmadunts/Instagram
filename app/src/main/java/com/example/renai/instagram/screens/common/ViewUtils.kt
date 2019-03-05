package com.example.renai.instagram.screens.common

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.renai.instagram.R


fun Context.showToast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    text?.let { Toast.makeText(this, it, duration).show() }
}

fun coordinateBtnAndInputs(btn: View, vararg inputs: EditText) {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = inputs.all { it.text.isNotEmpty() and it.text.isNotBlank() }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    inputs.forEach { it.addTextChangedListener(watcher) }
    btn.isEnabled = inputs.all { it.text.isNotEmpty() }
}

fun TextView.setCaptionText(username: String, caption: String) {
    val usernameSpannable = SpannableString(username)
    usernameSpannable.setSpan(
        StyleSpan(Typeface.BOLD), 0, usernameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    usernameSpannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            widget.context.showToast(context.getString(R.string.username_is_clicked))
        }

        override fun updateDrawState(ds: TextPaint) {}
    }, 0, usernameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = SpannableStringBuilder().append(usernameSpannable).append(" ").append(caption)
    movementMethod = LinkMovementMethod.getInstance()
}

fun ImageView.loadImage(image: String?) =
    ifNotDestroyed {
        GlideApp.with(this).load(image).centerCrop().into(this)
    }

fun ImageView.loadUserPhoto(photoUrl: String?) =
    ifNotDestroyed {
        GlideApp.with(this).load(photoUrl).fallback(R.drawable.person).into(this)
    }

fun Editable.toStringOrNull(): String? {
    val str = toString()
    return if (str.isEmpty()) null else str
}

private fun View.ifNotDestroyed(block: () -> Unit) {
    if (!(context as Activity).isDestroyed) {
        block()
    }
}

