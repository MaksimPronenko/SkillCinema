package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.FilmDbViewed

class ViewedAdapter(
    val limited: Boolean,
    private val onClick: (FilmDbViewed) -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
    private var data: List<FilmDbViewed> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<FilmDbViewed>) {
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
        val filmDbViewed = data.getOrNull(position)
        with(holder.binding) {
            if (filmDbViewed != null) {
                title.text = filmDbViewed.filmDb.filmTable.name
                genres.text = filmDbViewed.filmDb.genres.joinToString(", ") { it.genre }
                viewed.isVisible = filmDbViewed.viewed
                val image: String =
                    filmDbViewed.filmDb.filmTable.posterSmall.ifEmpty { filmDbViewed.filmDb.filmTable.poster }
                Glide
                    .with(poster.context)
                    .load(image)
                    .into(poster)
                if (filmDbViewed.filmDb.filmTable.rating != null) {
                    ratingFrame.isVisible = true
                    rating.text = filmDbViewed.filmDb.filmTable.rating.toString()
                } else {
                    ratingFrame.isVisible = false
                }
            }
            holder.binding.root.setOnClickListener {
                filmDbViewed?.let {
                    onClick(it)
                }
            }
        }
    }
}