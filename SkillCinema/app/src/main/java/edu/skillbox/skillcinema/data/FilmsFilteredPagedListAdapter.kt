package edu.skillbox.skillcinema.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.FilmFiltered

class FilmsFilteredPagedListAdapter(
    private val onClick: (FilmFiltered) -> Unit
) : PagingDataAdapter<FilmFiltered, FilmViewHolder>(FilmsFilteredDiffUtilCallback()) {

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
        val item = getItem(position)
        with(holder.binding) {
            title.text = if (item == null) "Ошибка загрузки"
            else if (!item.nameRu.isNullOrEmpty()) item.nameRu
            else if (!item.nameEn.isNullOrEmpty()) item.nameEn
            else if (!item.nameOriginal.isNullOrEmpty()) item.nameOriginal
            else "Наименование неизвестно"
            genres.text = item?.genres?.joinToString(", ") { it.genre }
            item?.let {
                val image: String = it.posterUrlPreview.ifEmpty { it.posterUrl }
                Glide
                    .with(poster.context)
                    .load(image)
                    .into(poster)
            }
            if (item?.ratingKinopoisk != null) {
                ratingFrame.isGone = false
                rating.text = item.ratingKinopoisk.toString()
            } else {
                ratingFrame.isGone = true
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}