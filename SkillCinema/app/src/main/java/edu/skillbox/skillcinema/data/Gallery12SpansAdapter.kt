package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.Gallery12SpansItemBinding
import edu.skillbox.skillcinema.models.ImageTable

class Gallery12SpansAdapter(
    private val dpToPix: Float,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<Gallery12SpansViewHolder>() {

    private var data: List<ImageTable> = emptyList()

    private val TYPE_ITEM_SMALL = 0
    private val TYPE_ITEM_BIG = 1

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<ImageTable>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Gallery12SpansViewHolder {

//        return when (viewType) {
//            0 -> Gallery12SpansViewHolder(
//                GalleryItemSmallBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//            else -> Gallery12SpansViewHolder(
//                GalleryItemBigBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//        }

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
            0 -> TYPE_ITEM_BIG
            else -> TYPE_ITEM_SMALL
        }
    }
}

//class GalleryItemSmallViewHolder(val binding: GalleryItemSmallBinding) :
//    RecyclerView.ViewHolder(binding.root)
//
//class GalleryItemBigViewHolder(val binding: GalleryItemBigBinding) :
//    RecyclerView.ViewHolder(binding.root)

//class Gallery12SpansViewHolder : RecyclerView.ViewHolder {
//
//    var bindingSmallItem: GalleryItemSmallBinding? = null
//    var bindingBigItem: GalleryItemBigBinding? = null
//
//    constructor(v: View) : super(v) {
//        bindingSmallItem = v.findViewById(R.id.text1)
//        bindingBigItem = v.findViewById(R.id.text2)
//    }
//}

class Gallery12SpansViewHolder(val binding: Gallery12SpansItemBinding) :
    RecyclerView.ViewHolder(binding.root)