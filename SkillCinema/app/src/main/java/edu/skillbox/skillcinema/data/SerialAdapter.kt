package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import edu.skillbox.skillcinema.databinding.EpisodeItemBinding
import edu.skillbox.skillcinema.models.Episode

class SerialAdapter : RecyclerView.Adapter<EpisodeViewHolder>() {
    private var data: List<Episode> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Episode>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            EpisodeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val item = data.getOrNull(position)
        if (item != null) {
            with(holder.binding) {
                val name = item.nameRu ?: item.nameEn ?: ""
                val episodeNumberAndName = "${item.episodeNumber} серия. $name"
                numberAndName.text = episodeNumberAndName

                if (!item.synopsis.isNullOrBlank()) {
                    synopsis.isGone = false
                    synopsis.text = item.synopsis
                }
                else synopsis.isGone = true

                if (!item.releaseDate.isNullOrBlank()) {
                    date.isGone = false
                    date.text = item.releaseDate
                }
                else date.isGone = true
            }
        }
    }
}

class EpisodeViewHolder(val binding: EpisodeItemBinding) :
    RecyclerView.ViewHolder(binding.root)