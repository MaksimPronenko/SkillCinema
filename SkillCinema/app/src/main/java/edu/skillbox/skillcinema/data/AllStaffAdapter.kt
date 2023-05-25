package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.AllStaffItemBinding
import edu.skillbox.skillcinema.models.StaffInfo

private const val TAG = "AllStaff.Adapter"

class AllStaffAdapter(
    private val onClick: (StaffInfo) -> Unit
) : RecyclerView.Adapter<AllStaffViewHolder>() {
    private var data: List<StaffInfo> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(receivedData: List<StaffInfo>) {
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
        with(holder.binding) {
            staffName.text = item?.nameRu ?: item?.nameEn ?: ""
            role.text = item?.description ?: item?.professionText
            item?.let {
                Glide
                    .with(photo.context)
                    .load(it.posterUrl)
                    .into(photo)
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}

class AllStaffViewHolder(val binding: AllStaffItemBinding) :
    RecyclerView.ViewHolder(binding.root)