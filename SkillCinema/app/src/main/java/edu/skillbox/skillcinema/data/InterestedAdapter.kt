package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.databinding.FilmItemClearBinding
import edu.skillbox.skillcinema.models.*

private const val TAG = "Interested.Adapter"

class InterestedAdapter(
    val limited: Boolean,
    private val onFilmClick: (FilmItemData) -> Unit,
    private val onSerialClick: (FilmItemData) -> Unit,
    private val onPersonClick: (PersonTable) -> Unit,
    private val clear: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data: List<RecyclerViewItem> = emptyList()
    private var mutableData: MutableList<RecyclerViewItem> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterData(receivedData: List<RecyclerViewItem>) {
        Log.d(TAG, "В адаптер пришёл список размера: ${receivedData.size}")
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
        if (item != null && item is Interested && holder is FilmViewHolder) {
            when (item.type) {
                0, 1 -> {
                    if (item.interestedViewItem is FilmItemData) {
                        val filmItemData = item.interestedViewItem
                        with(holder.binding) {
                            title.text = filmItemData.name
                            genres.text = filmItemData.genres
                            viewed.isVisible = filmItemData.viewed
                            Glide
                                .with(poster.context)
                                .load(filmItemData.poster)
                                .into(poster)
                            if (filmItemData.rating != null) {
                                ratingFrame.isVisible = true
                                rating.text = filmItemData.rating
                            } else ratingFrame.isVisible = false
                        }
                    }
                }
                2 -> {
                    if (item.interestedViewItem is PersonTable) {
                        val personTable = item.interestedViewItem
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
            when (item.type) {
                0 -> holder.binding.root.setOnClickListener {
                    onFilmClick(item.interestedViewItem as FilmItemData)
                }
                1 -> holder.binding.root.setOnClickListener {
                    onSerialClick(item.interestedViewItem as FilmItemData)
                }
                2 -> holder.binding.root.setOnClickListener {
                    onPersonClick(item.interestedViewItem as PersonTable)
                }
            }
        }
        if (holder is ClearViewHolder && item is EmptyRecyclerItem) {
            with(holder.binding) {
                clearButton.setOnClickListener {
                    clear()
                }
            }
        }
    }
}