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
import edu.skillbox.skillcinema.models.InterestedItem
import edu.skillbox.skillcinema.models.PersonTable

class InterestedAdapter(
    private val onFilmClick: (FilmDbViewed) -> Unit,
    private val onSerialClick: (FilmDbViewed) -> Unit,
    private val onPersonClick: (PersonTable) -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {

    private var data: List<Pair<Int, InterestedItem>> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<Pair<Int, InterestedItem>>) {
        data = receivedData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

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
        val item = data.getOrNull(position)
        if (item != null) {
            when (item.first) {
                0, 1 -> {
                    if (item.second is FilmDbViewed) {
                        val filmDbViewed = item.second as FilmDbViewed
                        with(holder.binding) {
                            title.text = filmDbViewed.filmDb.filmTable.name
                            genres.text = filmDbViewed.filmDb.genres.joinToString(", ") { it.genre }
                            val image: String =
                                filmDbViewed.filmDb.filmTable.posterSmall.ifEmpty { filmDbViewed.filmDb.filmTable.poster }
                            Glide
                                .with(poster.context)
                                .load(image)
                                .into(poster)
                            if (filmDbViewed.filmDb.filmTable.rating != null) {
                                ratingFrame.isGone = false
                                rating.text = filmDbViewed.filmDb.filmTable.rating.toString()
                            } else ratingFrame.isGone = true
                            viewed.isVisible = filmDbViewed.viewed
                        }
                    }
                }
                2 -> {
                    if (item.second is PersonTable) {
                        val personTable = item.second as PersonTable
                        with(holder.binding) {
                            title.text = personTable.name
                            genres.text = personTable.profession
                            Glide
                                .with(poster.context)
                                .load(personTable.posterUrl)
                                .into(poster)
                            ratingFrame.isVisible = false
                            viewed.isVisible = false
                        }
                    }
                }
            }
            when (item.first) {
                0 -> holder.binding.root.setOnClickListener {
                    onFilmClick(item.second as FilmDbViewed)
                }
                1 -> holder.binding.root.setOnClickListener {
                    onSerialClick(item.second as FilmDbViewed)
                }
                2 -> holder.binding.root.setOnClickListener {
                    onPersonClick(item.second as PersonTable)
                }
            }
        }
    }
}