package edu.skillbox.skillcinema.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.Gallery12SpansItemBinding
import edu.skillbox.skillcinema.models.filmAndSerial.image.ImageTable

class Gallery12SpansAdapter(
    private val dpToPix: Float,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<Gallery12SpansViewHolder>() {

    private var data: List<ImageTable> = emptyList()

    private val typeItemSmall = 0
    private val typeItemBig = 1

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ImageTable>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Gallery12SpansViewHolder {
        return Gallery12SpansViewHolder(
            Gallery12SpansItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Gallery12SpansViewHolder, position: Int) {
        val item = data.getOrNull(position)

        if (item != null) {
            when (getItemViewType(position)) {
                0 -> {
                    holder.binding.image.layoutParams.height = (82 * dpToPix).toInt()
                }
                else -> {
                    holder.binding.image.layoutParams.height = (173 * dpToPix).toInt()
                }
            }

            with(holder.binding) {
                Glide
                    .with(image.context)
                    .load(item.preview)
                    .into(image)
            }

            holder.binding.root.setOnClickListener {
                val currentImage: String = item.image
                onClick(currentImage)
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return when (position % 3) {
            0 -> typeItemBig
            else -> typeItemSmall
        }
    }
}

class Gallery12SpansViewHolder(val binding: Gallery12SpansItemBinding) :
    RecyclerView.ViewHolder(binding.root)