package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.ListFavoritesBinding
import com.example.movieapp.db.MovieEntity
import com.example.movieapp.utils.Constants
import javax.inject.Inject

class FavouriteMovieAdapter @Inject constructor() :
    RecyclerView.Adapter<FavouriteMovieAdapter.FavouriteMovieViewHolder>() {
    var favoriteMovie: List<MovieEntity> = ArrayList()

    inner class FavouriteMovieViewHolder(val binding: ListFavoritesBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setFavoriteMovieList(favoriteMovie: List<MovieEntity>) {
        this.favoriteMovie = favoriteMovie
        notifyDataSetChanged()
    }

    fun getMelaByPosition(position: Int):MovieEntity{
        return favoriteMovie[position]
    }


//    private val differCallBack = object : DiffUtil.ItemCallback<MovieEntity>() {
//        override fun areItemsTheSame(
//            oldItem: MovieEntity, newItem: MovieEntity
//        ): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(
//            oldItem: MovieEntity, newItem: MovieEntity
//        ): Boolean {
//            return oldItem == newItem
//        }
//    }
//    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteMovieViewHolder {
        return FavouriteMovieViewHolder(
            ListFavoritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    }

    override fun onBindViewHolder(holder: FavouriteMovieViewHolder, position: Int) {
        val movie = favoriteMovie[position] // Corrected this line
        Glide.with(holder.itemView.context)  // Use holder.itemView.context to obtain the context
            .load(Constants.POSTER_BASE_URL + movie.posterPath)
            .into(holder.binding.imgFavouriteMovie)

        holder.binding.tvMovieName.text = movie.title
        holder.binding.tvRate.text = movie.voteAverage
        holder.binding.tvLang.text = movie.originalLanguage
        holder.binding.tvMovieDateRelease.text = movie.releaseDate
        holder.binding.tvRunTime.text=movie.runTime

        // Set click listener for the item view
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(movie)
        }
    }

    override fun getItemCount(): Int {
        return favoriteMovie.size // Changed from differ.currentList.size

    }

    private var onItemClickListener: ((MovieEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (MovieEntity) -> Unit) {
        onItemClickListener = listener
    }
}
