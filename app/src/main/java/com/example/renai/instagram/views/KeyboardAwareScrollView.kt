package com.example.renai.instagram.views

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ScrollView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class KeyboardAwareScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs),
    KeyboardVisibilityEventListener {
    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen) {
            scrollTo(0, bottom)
        } else {
            scrollTo(0, top)
        }
    }

    init {
        isFillViewport = true
        isVerticalScrollBarEnabled = false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        KeyboardVisibilityEvent.setEventListener(context as Activity, this)
        KeyboardVisibilityEvent.setEventListener(context as Activity?, this)

    }
}