package edu.skillbox.skillcinema.data

import androidx.recyclerview.widget.DiffUtil
import edu.skillbox.skillcinema.models.FilmFiltered

class FilmsFilteredDiffUtilCallback : DiffUtil.ItemCallback<FilmFiltered>() {
    override fun areItemsTheSame(oldItem: FilmFiltered, newItem: FilmFiltered): Boolean =
        oldItem.kinopoiskId == newItem.kinopoiskId

    override fun areContentsTheSame(oldItem: FilmFiltered, newItem: FilmFiltered): Boolean =
        oldItem == newItem
}