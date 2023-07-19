package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.StaffItemBinding
import edu.skillbox.skillcinema.models.StaffTable

class StaffAdapter(
    private val maxSize: Int,
    private val onClick: (StaffTable) -> Unit
) : RecyclerView.Adapter<StaffViewHolder>() {
    private var data: List<StaffTable> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<StaffTable>) {
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
        if (item != null) {
            with(holder.binding) {
                staffName.text = item.name
                role.text = item.description ?: item.professionText
                Glide
                    .with(poster.context)
                    .load(item.posterUrl)
                    .into(poster)
            }
            holder.binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}

class StaffViewHolder(val binding: StaffItemBinding) :
    RecyclerView.ViewHolder(binding.root)

