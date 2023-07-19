package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.databinding.FilmItemClearBinding
import edu.skillbox.skillcinema.models.EmptyRecyclerItem
import edu.skillbox.skillcinema.models.FilmItemData
import edu.skillbox.skillcinema.models.RecyclerViewItem

private const val TAG = "CollectionFilms.Adapter"

const val VIEW_TYPE_CLEAR = 2

class CollectionFilmsAdapter(
    val limited: Boolean,
    val viewedOrCollection: Boolean,
    val context: Context,
    private val onClick: (FilmItemData) -> Unit,
    private val clear: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: List<RecyclerViewItem> = listOf()
    private var mutableData: MutableList<RecyclerViewItem> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterData(receivedData: List<FilmItemData>) {
        Log.d(TAG, "В адаптер пришёл список размера: ${receivedData.size}")
        Log.d(TAG, "В адаптер пришёл список: $receivedData")
        mutableData = if (limited && receivedData.size > 20) {
            receivedData.subList(0, 20).toMutableList()
        } else {
            receivedData.toMutableList()
        }
        mutableData.add(EmptyRecyclerItem())
        data = mutableData.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return if (position != data.lastIndex && data.size > 1) VIEW_TYPE_FILM
        else VIEW_TYPE_CLEAR
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
            ClearViewHolder(
                FilmItemClearBinding.inflate(
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
        if (holder is ClearViewHolder && item is EmptyRecyclerItem) {
            with(holder.binding) {
                buttonText.text = if (viewedOrCollection) context.getString(R.string.clear_history)
                else context.getString(R.string.clear_collection)
                clearButton.setOnClickListener {
                    clear()
                }
            }
        }
    }
}

class ClearViewHolder(val binding: FilmItemClearBinding) :
    RecyclerView.ViewHolder(binding.root)