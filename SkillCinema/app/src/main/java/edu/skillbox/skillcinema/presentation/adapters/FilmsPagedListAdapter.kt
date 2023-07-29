package edu.skillbox.skillcinema.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.data.FilmsPagedDiffUtilCallback
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.filmAndSerial.film.FilmItemData

class FilmsPagedListAdapter(
    private val onClick: (FilmItemData) -> Unit
) : PagingDataAdapter<FilmItemData, FilmViewHolder>(FilmsPagedDiffUtilCallback()) {

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
        val filmItemData = getItem(position)
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