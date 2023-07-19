package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.GalleryItemBinding
import edu.skillbox.skillcinema.models.ImageTable

class GalleryAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<GalleryViewHolder>() {
    private var data: List<ImageTable> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ImageTable>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (data.size < 20) data.size
        else 20
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            GalleryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = data.getOrNull(position)
        if (item != null) {
            with(holder.binding) {
                Glide
                    .with(image.context)
                    .load(item.preview)
                    .into(image)
            }
            holder.binding.root.setOnClickListener {
                onClick(item.image)
            }
        }
    }
}

class GalleryViewHolder(val binding: GalleryItemBinding) :
    RecyclerView.ViewHolder(binding.root)

