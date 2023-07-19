package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmOfStaffItemBinding
import edu.skillbox.skillcinema.models.FilmItemData

private const val TAG = "Filmography.Adapter"

class StaffFilmsAdapter(
    private val onClick: (FilmItemData) -> Unit
) : RecyclerView.Adapter<StaffFilmViewHolder>() {

    private var data: List<FilmItemData> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<FilmItemData>) {
        data = receivedData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

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
        val item = data.getOrNull(position)
        if (item != null) {
            with(holder.binding) {
                filmName.text = item.name
                filmInfo.text = if (item.year == null) item.genres
                else "${item.year}, ${item.genres}"
                viewed.isVisible = item.viewed
                Glide
                    .with(poster.context)
                    .load(item.poster)
                    .into(poster)
                if (item.rating != null) {
                    ratingFrame.isVisible = true
                    rating.text = item.rating
                } else ratingFrame.isVisible = false
            }
            holder.binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}

class StaffFilmViewHolder(val binding: FilmOfStaffItemBinding) :
    RecyclerView.ViewHolder(binding.root)