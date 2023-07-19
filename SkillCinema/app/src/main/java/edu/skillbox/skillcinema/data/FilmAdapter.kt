package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.databinding.FilmItemShowAllBinding
import edu.skillbox.skillcinema.models.EmptyRecyclerItem
import edu.skillbox.skillcinema.models.FilmItemData
import edu.skillbox.skillcinema.models.RecyclerViewItem

private const val TAG = "Film.Adapter"

const val VIEW_TYPE_FILM = 0
const val VIEW_TYPE_SHOW_ALL = 1

class FilmAdapter(
    val limited: Boolean,
    private val onClick: (FilmItemData) -> Unit,
    private val showAll: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: List<RecyclerViewItem> = listOf()
    private var mutableData: MutableList<RecyclerViewItem> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterData(receivedData: List<FilmItemData>) {
        Log.d(TAG, "В адаптер пришёл список размера: ${receivedData.size}")
        if (limited) {
            mutableData = if (receivedData.size > 20 ) receivedData.subList(0, 20).toMutableList()
            else receivedData.toMutableList()
            mutableData.add(EmptyRecyclerItem())
            data = mutableData.toList()
        } else {
            data = receivedData
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (!limited || data.size < 21) data.size
        else 21
    }

    override fun getItemViewType(position: Int): Int {
        return if (!limited || position != data.lastIndex) VIEW_TYPE_FILM
        else VIEW_TYPE_SHOW_ALL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_FILM) {
            FilmViewHolder(
                FilmItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ShowAllViewHolder(
                FilmItemShowAllBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data.getOrNull(position)
        if (holder is FilmViewHolder && item is FilmItemData) {
                with(holder.binding) {
                    title.text = item.name
                    genres.text = item.genres
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
        if (holder is ShowAllViewHolder && item is EmptyRecyclerItem) {
            with(holder.binding) {
                arrowButton.setOnClickListener {
                    showAll()
                }
            }
        }
    }
}

class FilmViewHolder(val binding: FilmItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class ShowAllViewHolder(val binding: FilmItemShowAllBinding) :
    RecyclerView.ViewHolder(binding.root)