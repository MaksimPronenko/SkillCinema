package edu.skillbox.skillcinema.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmOfStaffItemBinding
import edu.skillbox.skillcinema.models.FilmItemData

class SearchPagedListAdapter(
    private val onClick: (FilmItemData) -> Unit
) : PagingDataAdapter<FilmItemData, StaffFilmViewHolder>(FilmsFilteredDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffFilmViewHolder {
        return StaffFilmViewHolder(
            FilmOfStaffItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StaffFilmViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            if (item != null) {
                filmName.text = item.name
                val genresToString = item.genres
                filmInfo.text = if (item.year == null) genresToString
                else "${item.year}, $genresToString"
                viewed.isVisible = item.viewed
                Glide
                    .with(poster.context)
                    .load(item.poster)
                    .into(poster)
                if (item.rating != null) {
                    ratingFrame.isVisible = true
                    rating.text = item.rating
                } else ratingFrame.isVisible = false
            } else {
                filmName.text = "Ошибка загрузки"
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}