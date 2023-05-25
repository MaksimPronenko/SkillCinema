package edu.skillbox.skillcinema.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmOfStaffItemBinding
import edu.skillbox.skillcinema.models.FilmFiltered

class SearchPagedListAdapter(
    private val onClick: (FilmFiltered) -> Unit
) : PagingDataAdapter<FilmFiltered, StaffFilmViewHolder>(FilmsFilteredDiffUtilCallback()) {

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
                filmName.text = if (!item.nameRu.isNullOrEmpty()) item.nameRu
                else if (!item.nameEn.isNullOrEmpty()) item.nameEn
                else if (!item.nameOriginal.isNullOrEmpty()) item.nameOriginal
                else ""
                val genresToString = item.genres.joinToString(", ") { it.genre }
                filmInfo.text = if (item.year == null) genresToString
                else "${item.year}, $genresToString"
                val image: String = item.posterUrlPreview.ifEmpty { item.posterUrl }
                Glide
                    .with(poster.context)
                    .load(image)
                    .into(poster)
                if (item.ratingKinopoisk != null) {
                    ratingFrame.isGone = false
                    rating.text = item.ratingKinopoisk.toString()
                } else ratingFrame.isGone = true
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