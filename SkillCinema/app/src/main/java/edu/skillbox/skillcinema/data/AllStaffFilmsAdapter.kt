package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.FilmOfStaff

private const val TAG = "AllFilmsOfStaff.Adapter"

class AllStaffFilmsAdapter(
    private val onClick: (FilmOfStaff) -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
    private var data: List<FilmOfStaff> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<FilmOfStaff>) {
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
        val filmOfStaff = data.getOrNull(position)
        with(holder.binding) {
            if (filmOfStaff != null) {
                title.text = filmOfStaff.name
                genres.text = filmOfStaff.genres
                Glide
                    .with(poster.context)
                    .load(filmOfStaff.poster)
                    .into(poster)
                if (filmOfStaff.rating != null) {
                    ratingFrame.isGone = false
                    rating.text = filmOfStaff.rating.toString()
                } else ratingFrame.isGone = true
            }
        }
        holder.binding.root.setOnClickListener {
            filmOfStaff?.let {
                onClick(it)
            }
        }
    }
}