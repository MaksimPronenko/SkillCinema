package edu.skillbox.skillcinema.data

import androidx.recyclerview.widget.DiffUtil
import edu.skillbox.skillcinema.models.FilmTop

class FilmsTopDiffUtilCallback : DiffUtil.ItemCallback<FilmTop>() {
    override fun areItemsTheSame(oldItem: FilmTop, newItem: FilmTop): Boolean =
        oldItem.filmId == newItem.filmId

    override fun areContentsTheSame(oldItem: FilmTop, newItem: FilmTop): Boolean =
        oldItem == newItem
}