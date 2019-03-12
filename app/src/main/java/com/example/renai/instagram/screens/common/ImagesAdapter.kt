package com.example.renai.instagram.screens.common

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.example.renai.instagram.R
import com.example.renai.instagram.common.SimpleCallback

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    private var images = listOf<String>()
    fun updateImages(newImages: List<String>) {
        val diffResult = DiffUtil.calculateDiff(
            SimpleCallback(
                images,
                newImages,
                { it })
        )
        this.images = newImages
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(image)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }
}