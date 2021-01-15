package com.example.firebasesrorage2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.image_item.view.*

class RecAdapter : RecyclerView.Adapter<RecAdapter.ImageViewHolder>() {

    private var list: List<MapImage> = ArrayList()

    fun setList(list: MutableList<MapImage>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.itemView.apply {
            Glide
                .with(holder.itemView)
                .load(list[position].url)
                .into(imageViewImageItem)
            textViewImageName.text = list[position].name
            textViewImageDescription.text = list[position].description
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}