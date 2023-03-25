package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.GalleryItemBinding
import edu.skillbox.skillcinema.models.ImageWithType

class GalleryAdapter(
    val limited: Boolean,
    private val onClick: (ImageWithType) -> Unit
) : RecyclerView.Adapter<GalleryViewHolder>() {
    private var data: List<ImageWithType> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ImageWithType>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (!limited || data.size < 20) data.size
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
        with(holder.binding) {
            item?.let {
                Glide
                    .with(image.context)
                    .load(it.previewUrl)
                    .into(image)
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}

class GalleryViewHolder(val binding: GalleryItemBinding) :
    RecyclerView.ViewHolder(binding.root)

