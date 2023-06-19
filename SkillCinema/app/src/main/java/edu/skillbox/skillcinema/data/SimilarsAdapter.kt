package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.SimilarFilm

class SimilarsAdapter(
    val limited: Boolean,
    private val onClick: (SimilarFilm) -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
    private var data: List<SimilarFilm> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<SimilarFilm>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (!limited || data.size < 20) data.size
        else 20
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return FilmViewHolder(
            FilmItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            title.text = if (item == null) "Ошибка загрузки"
            else if (!item.nameRu.isNullOrEmpty()) item.nameRu
            else if (!item.nameEn.isNullOrEmpty()) item.nameEn
            else if (!item.nameOriginal.isNullOrEmpty()) item.nameOriginal
            else "Наименование неизвестно"
            genres.isGone = true
            item?.let {
                Glide
                    .with(poster.context)
                    .load(it.posterUrlPreview)
                    .into(poster)
            }
            ratingFrame.isGone = true
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(item)
            }
        }
    }
}