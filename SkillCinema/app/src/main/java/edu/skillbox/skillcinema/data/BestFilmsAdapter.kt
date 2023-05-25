package edu.skillbox.skillcinema.data

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.skillbox.skillcinema.databinding.FilmItemBinding
import edu.skillbox.skillcinema.models.FilmOfStaff

private const val TAG = "Staff.Adapter"

class BestFilmsAdapter(
    val limited: Boolean,
    private val onClick: (FilmOfStaff) -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
//    private var data: List<FilmOfStaff> = emptyList()
//    @SuppressLint("NotifyDataSetChanged")
//    fun setData(data: List<FilmOfStaff>) {
//        this.data = data
//        notifyDataSetChanged()
//    }

    //    private var data: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
    private var data: List<FilmOfStaff> = emptyList()
//    private var mutableData: MutableList<FilmOfStaff> = emptyList<FilmOfStaff>().toMutableList()
//    private var addedId: MutableList<Int> = emptyList<Int>().toMutableList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<FilmOfStaff>) {
        Log.d(TAG, "В адаптер пришёл список размера: ${data.size}")
        this.data = data
        notifyDataSetChanged()
    }

//    fun addItem(item: FilmOfStaff) {
//        Log.d(TAG, "В адаптер пришёл ${item.filmId}")
//        mutableData.add(item)
//        data = mutableData.toList()
//        val index = data.size - 1
//        Log.d(TAG, "Индекс добавляемого в адаптер ${item.filmId} равен $index")
//        notifyItemInserted(index)
//    }
//    fun addItem(item: FilmOfStaff) {
//        Log.d(TAG, "Сработал addItem ${item.filmId}")
//        var alreadyAdded = false
//        Log.d(TAG, "Размер addedId при проверке: ${addedId.size}")
//        addedId.forEach {
//            if (it == item.filmId) alreadyAdded = true
//        }
//    }
////        val alreadyAdded = data.filter { filmOfStaff ->
////            filmOfStaff.filmId == item.filmId
////        }
//        Log.d(TAG, "alreadyAdded ${item.filmId}: $alreadyAdded")
//        if (!alreadyAdded) {
//            Log.d(TAG, "Не был раньше добавлен ${item.filmId}")
////            data.add(item)
//            notifyItemInserted(data.size)
//            Log.d(TAG, "Размер data после добавления ${item.filmId}: ${data.size}")
//            addedId.add(item.filmId)
//        }
//    }

        override fun getItemCount(): Int {
            return if (!limited || data.size < 10) data.size
            else 10
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
            val filmOfStaff = data.getOrNull(position)
            with(holder.binding) {
                if (filmOfStaff != null) {
                    title.text = filmOfStaff.name
                    genres.text = filmOfStaff.genres
                    Glide
                        .with(poster.context)
                        .load(filmOfStaff.poster)
                        .into(poster)
                    if (filmOfStaff.rating != null) {
                        ratingFrame.isGone = false
                        rating.text = filmOfStaff.rating.toString()
                    } else ratingFrame.isGone = true
                }
            }
            holder.binding.root.setOnClickListener {
                filmOfStaff?.let {
                    onClick(it)
                }
            }
        }
    }