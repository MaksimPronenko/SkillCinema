package edu.skillbox.skillcinema.data

import androidx.recyclerview.widget.DiffUtil
import edu.skillbox.skillcinema.models.FilmItemData

class FilmsFilteredDiffUtilCallback : DiffUtil.ItemCallback<FilmItemData>() {
    override fun areItemsTheSame(oldItem: FilmItemData, newItem: FilmItemData): Boolean =
        oldItem.filmId == newItem.filmId

    override fun areContentsTheSame(oldItem: FilmItemData, newItem: FilmItemData): Boolean =
        oldItem == newItem
}