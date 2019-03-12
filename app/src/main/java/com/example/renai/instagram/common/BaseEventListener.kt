package com.example.renai.instagram.common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry

abstract class BaseEventListener : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    init {
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }
}