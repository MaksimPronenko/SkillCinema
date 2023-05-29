package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import edu.skillbox.skillcinema.R
import edu.skillbox.skillcinema.databinding.CollectionItemBinding
import edu.skillbox.skillcinema.models.CollectionInfo

private const val TAG = "Collection.Adapter"

class CollectionAdapter(
    private val onOpenCollection: (CollectionInfo) -> Unit,
    private val onDeleteCollection: (CollectionInfo) -> Unit
) : RecyclerView.Adapter<CollectionViewHolder>() {

    private var data: List<CollectionInfo> = emptyList()
    private var mutableData: MutableList<CollectionInfo> = emptyList<CollectionInfo>().toMutableList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<CollectionInfo>) {
        data = receivedData
        notifyDataSetChanged()
    }

    fun addItem(item: CollectionInfo) {
        mutableData.add(item)
        data = mutableData.toList()
        val index = data.size - 1
        notifyItemInserted(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(item: CollectionInfo) {
        mutableData.remove(item)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(
            CollectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = data.getOrNull(position)
        with(holder.binding) {
            if (collection != null) {
                collectionName.text = collection.collectionName
                when(collection.collectionName) {
                    "Любимое" -> {
                        collectionImage.setImageResource(R.drawable.favorite)
                        deleteButton.isGone = true
                    }
                    "Хочу посмотреть" -> {
                        collectionImage.setImageResource(R.drawable.wanted_to_watch)
                        deleteButton.isGone = true
                    }
                    else -> {
                        collectionImage.setImageResource(R.drawable.profile)
                        deleteButton.isGone = false
                    }
                }
                quantity.text = collection.filmsQuantity.toString()
            }
        }
        holder.binding.root.setOnClickListener {
            collection?.let {
                onOpenCollection(it)
            }
        }
        holder.binding.deleteButton.setOnClickListener {
            collection?.let {
                onDeleteCollection(it)
            }
        }
    }
}

class CollectionViewHolder(val binding: CollectionItemBinding) :
    RecyclerView.ViewHolder(binding.root)