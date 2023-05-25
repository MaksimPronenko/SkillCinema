package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.StaffItemBinding
import edu.skillbox.skillcinema.models.StaffInfo

class StaffInfoAdapter(
    private val maxSize: Int,
    private val onClick: (StaffInfo) -> Unit
) : RecyclerView.Adapter<StaffViewHolder>() {
    private var data: List<StaffInfo> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<StaffInfo>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (data.size < maxSize) data.size
        else maxSize
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        return StaffViewHolder(
            StaffItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            staffName.text = item?.nameRu ?: item?.nameEn ?: ""
            role.text = item?.description ?: item?.professionText
            item?.let {
                Glide
                    .with(poster.context)
                    .load(it.posterUrl)
                    .into(poster)
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}

class StaffViewHolder(val binding: StaffItemBinding) :
    RecyclerView.ViewHolder(binding.root)

