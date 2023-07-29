package edu.skillbox.skillcinema.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.AllStaffItemBinding
import edu.skillbox.skillcinema.models.filmAndSerial.staff.StaffTable

class AllStaffAdapter(
    private val onClick: (StaffTable) -> Unit
) : RecyclerView.Adapter<AllStaffViewHolder>() {
    private var data: List<StaffTable> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<StaffTable>) {
        data = receivedData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllStaffViewHolder {
        return AllStaffViewHolder(
            AllStaffItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AllStaffViewHolder, position: Int) {
        val item = data.getOrNull(position)
        if (item != null) {
            with(holder.binding) {
                staffName.text = item.name
                role.text = item.description ?: item.professionText
                    Glide
                        .with(photo.context)
                        .load(item.posterUrl)
                        .into(photo)
            }
            holder.binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}

class AllStaffViewHolder(val binding: AllStaffItemBinding) :
    RecyclerView.ViewHolder(binding.root)