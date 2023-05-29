package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.CollectionExistingItemBinding
import edu.skillbox.skillcinema.models.CollectionFilm

private const val TAG = "CollectionFilm.Adapter"

class CollectionFilmAdapter(
    private val onCollectionItemClick: (CollectionFilm) -> Unit
) : RecyclerView.Adapter<CollectionFilmViewHolder>() {

    private var data: List<CollectionFilm> = emptyList()
//    private var mutableData: MutableList<CollectionFilm> = emptyList<CollectionFilm>().toMutableList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<CollectionFilm>) {
        this.data = receivedData
        Log.d(TAG, "В адаптер пришёл список размера: ${data.size}")
        notifyDataSetChanged()
    }

//    fun addItem(item: CollectionFilm) {
//        mutableData.add(item)
//        data = mutableData.toList()
//        val index = data.size - 1
//        notifyItemInserted(index)
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun removeItem(item: CollectionFilm) {
//        mutableData.remove(item)
//        notifyDataSetChanged()
//    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionFilmViewHolder {
        return CollectionFilmViewHolder(
            CollectionExistingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CollectionFilmViewHolder, position: Int) {
        val collectionFilm = data.getOrNull(position)
        with(holder.binding) {
            if (collectionFilm != null) {
                if (collectionFilm.filmIncluded) {
                    collectionLabel.setImageResource(R.drawable.collection_chosen)
                } else {
                    collectionLabel.setImageResource(R.drawable.collection_not_chosen)
                }
                collectionName.text = collectionFilm.collectionName
                collectionQuantity.text = collectionFilm.filmsQuantity.toString()
            } else {
                Log.d(TAG, "collection = null")
            }
        }
        holder.binding.root.setOnClickListener {
            collectionFilm?.let {
                onCollectionItemClick(it)
            }
        }
    }
}

class CollectionFilmViewHolder(val binding: CollectionExistingItemBinding) :
    RecyclerView.ViewHolder(binding.root)