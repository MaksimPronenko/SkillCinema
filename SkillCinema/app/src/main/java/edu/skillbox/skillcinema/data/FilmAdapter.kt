package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.FilmItemData

class FilmAdapter(
    val limited: Boolean,
    private val onClick: (FilmItemData) -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
    private var data: List<FilmItemData> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<FilmItemData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (!limited || data.size < 20) data.size
        else 20
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return FilmViewHolder(
            FilmItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val filmItemData = data.getOrNull(position)
        if (filmItemData != null) {
            with(holder.binding) {
                title.text = filmItemData.name
                genres.text = filmItemData.genres
                viewed.isVisible = filmItemData.viewed
                Glide
                    .with(poster.context)
                    .load(filmItemData.poster)
                    .into(poster)
                if (filmItemData.rating != null) {
                    ratingFrame.isVisible = true
                    rating.text = filmItemData.rating
                } else ratingFrame.isVisible = false
            }
            holder.binding.root.setOnClickListener {
                onClick(filmItemData)
            }
        }
    }
}