package edu.skillbox.skillcinema.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.GalleryAllScreenItemBinding

class ImageAdapter : RecyclerView.Adapter<ImageViewHolder>() {
    private var data: List<String> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<String>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            GalleryAllScreenItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            item?.let {
                Glide
                    .with(image.context)
                    .load(it)
                    .into(image)
            }
        }
    }
}

class ImageViewHolder(val binding: GalleryAllScreenItemBinding) :
    RecyclerView.ViewHolder(binding.root)