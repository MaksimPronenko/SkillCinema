package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.FilmTable

class ViewedAdapter(
    val limited: Boolean,
    private val onClick: (FilmTable) -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
    private var data: List<FilmTable> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<FilmTable>) {
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
        val film = data.getOrNull(position)
        with(holder.binding) {
            if (film != null) {
                title.text = film.name
                genres.isGone = true
                val image: String = film.posterSmall.ifEmpty { film.poster }
                Glide
                    .with(poster.context)
                    .load(image)
                    .into(poster)
                if (film.rating != null) {
                    ratingFrame.isGone = false
                    rating.text = film.rating.toString()
                } else {
                    ratingFrame.isGone = true
                }
            }
            holder.binding.root.setOnClickListener {
                film?.let {
                    onClick(it)
                }
            }
        }
    }
}