package com.example.movieapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.ListGeneresBinding
import com.example.movieapp.model.genere.Genre
import com.example.movieapp.model.popularMovie.Result
import javax.inject.Inject

class GenerMovieAdapter @Inject constructor(): RecyclerView.Adapter<GenerMovieAdapter.GenerViewHolder>() {
    inner class GenerViewHolder(val binding: ListGeneresBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val genre = differ.currentList[position]
                    onItemClickListener?.invoke(genre)
                }
            }
        }
        fun bind(genre: Genre) {
            binding.tvGenre.text = genre.name

            // Change text color based on whether the genre is selected or not
            if (genre.id == selectedGenreId) {
                val color = ContextCompat.getColor(binding.root.context, R.color.themeColor)
                binding.tvGenre.setTextColor(color)
            } else {
                binding.tvGenre.setTextColor(Color.BLACK) // Change text color back to default for unselected genres
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenerViewHolder {
        return GenerViewHolder(ListGeneresBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: GenerViewHolder, position: Int) {
        val genre = differ.currentList[position]
        holder.binding.tvGenre.text = genre.name
        holder.bind(genre) // Call the bind function here

    }

    private var onItemClickListener: ((Genre) -> Unit)? = null

    fun setOnItemClickListener(listener: (Genre) -> Unit) {
        onItemClickListener = listener
    }

    private var selectedGenreId: Int? = null

    fun setSelectedGenreId(genreId: Int?) {
        selectedGenreId = genreId
        notifyDataSetChanged() // Notify adapter to redraw views
    }
}