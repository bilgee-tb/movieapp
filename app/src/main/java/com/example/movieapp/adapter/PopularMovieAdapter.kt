package com.example.movieapp.adapter

import android.content.Intent
import android.text.Layout.Directions
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.ListMoviesBinding
import com.example.movieapp.model.genere.Genre
import com.example.movieapp.model.popularMovie.Result
import com.example.movieapp.ui.fragment.HomeFragment
import com.example.movieapp.ui.fragment.HomeFragmentDirections
import com.example.movieapp.ui.fragment.MovieDetailFragment
import com.example.movieapp.utils.Constants
import javax.inject.Inject

class PopularMovieAdapter @Inject constructor() : RecyclerView.Adapter<PopularMovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ListMoviesBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)   // why this line?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            ListMoviesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load( Constants.POSTER_BASE_URL + movie.poster_path).into(holder.binding.imgMovie)
            holder.binding.tvMovieName.text = movie.title
            setOnClickListener {
                onItemClickListener?.invoke(movie)
            }
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    private var onItemClickListener: ((Result) -> Unit)? = null

    fun setOnItemClickListener(listener: (Result) -> Unit) {
        onItemClickListener = listener
    }
}