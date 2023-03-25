package edu.skillbox.skillcinema.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.FilmTop

class FilmsTopPagedListAdapter(
    private val onClick: (FilmTop) -> Unit
) : PagingDataAdapter<FilmTop, FilmViewHolder>(FilmsTopDiffUtilCallback()) {

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
            title.text = item?.nameRu ?: ""
            genres.text = item?.genres?.joinToString(", ") { it.genre }
            item?.let {
                Glide
                    .with(poster.context)
                    .load(it.posterUrlPreview)
                    .into(poster)
            }
            if (item?.rating != null)
                rating.text = item.rating
            else ratingFrame.isGone = true
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}