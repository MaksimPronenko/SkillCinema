package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmOfStaffItemBinding
import edu.skillbox.skillcinema.models.FilmOfStaff

private const val TAG = "Filmography.Adapter"

class StaffFilmsAdapter(
    private val onClick: (FilmOfStaff) -> Unit
) : RecyclerView.Adapter<StaffFilmViewHolder>() {
//    private var data: List<FilmOfStaff> = emptyList()
//    @SuppressLint("NotifyDataSetChanged")
//    fun setData(data: List<FilmOfStaff>) {
//        this.data = data
//        notifyDataSetChanged()
//    }

    private var data: List<FilmOfStaff> = emptyList()
//    private var mutableData: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<FilmOfStaff>) {
        data = receivedData
        notifyDataSetChanged()
    }

//    fun addItem(item: FilmOfStaff) {
//        mutableData.add(item)
//        data = mutableData.toList()
//        val index = data.size - 1
//        notifyItemInserted(index)
//    }

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
        val filmOfStaff = data.getOrNull(position)
        with(holder.binding) {
            if (filmOfStaff != null) {
                filmName.text = filmOfStaff.name
                filmInfo.text = if (filmOfStaff.year == null) filmOfStaff.genres
                else "${filmOfStaff.year}, ${filmOfStaff.genres}"
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

class StaffFilmViewHolder(val binding: FilmOfStaffItemBinding) :
    RecyclerView.ViewHolder(binding.root)