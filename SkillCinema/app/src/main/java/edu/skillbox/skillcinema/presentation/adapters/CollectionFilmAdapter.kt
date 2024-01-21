package edu.skillbox.skillcinema.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.CollectionCreateItemBinding
import edu.skillbox.skillcinema.databinding.CollectionExistingItemBinding
import edu.skillbox.skillcinema.models.collection.CollectionFilm
import edu.skillbox.skillcinema.models.collection.EmptyRecyclerItem
import edu.skillbox.skillcinema.models.collection.RecyclerViewItem

private const val TAG = "CollectionFilm.Adapter"

const val VIEW_TYPE_COLLECTION = 0
const val VIEW_TYPE_ADD_COLLECTION = 1

class CollectionFilmAdapter(
    private val onCollectionItemClick: (CollectionFilm) -> Unit,
    private val onAddCollectionItemClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = listOf<RecyclerViewItem>()
    private var mutableData = mutableListOf<RecyclerViewItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setAdapterData(receivedData: List<CollectionFilm>) {
        Log.d(TAG, "В адаптер пришёл список размера: ${receivedData.size}")
        mutableData = receivedData.toMutableList()
        mutableData.add(EmptyRecyclerItem())
        data = mutableData.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return if (position != data.lastIndex) VIEW_TYPE_COLLECTION
        else VIEW_TYPE_ADD_COLLECTION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_COLLECTION) {
            CollectionFilmViewHolder(
                CollectionExistingItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            CollectionCreateViewHolder(
                CollectionCreateItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder is CollectionFilmViewHolder && item is CollectionFilm) {
            with(holder.binding) {
                if (item.filmIncluded) {
                    collectionLabel.setImageResource(R.drawable.collection_chosen)
                } else {
                    collectionLabel.setImageResource(R.drawable.collection_not_chosen)
                }
                collectionName.text = item.collectionName
                collectionQuantity.text = item.filmsQuantity.toString()

                collectionButton.setOnClickListener {
                    onCollectionItemClick(item)
                }
            }
        }
        if (holder is CollectionCreateViewHolder && item is EmptyRecyclerItem) {
            with(holder.binding) {
                collectionButton.setOnClickListener {
                    onAddCollectionItemClick()
                }
            }
        }
    }
}

class CollectionFilmViewHolder(val binding: CollectionExistingItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class CollectionCreateViewHolder(val binding: CollectionCreateItemBinding) :
    RecyclerView.ViewHolder(binding.root)