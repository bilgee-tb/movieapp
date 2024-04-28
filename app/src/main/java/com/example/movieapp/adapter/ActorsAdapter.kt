package com.example.movieapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.ListActorsBinding
import com.example.movieapp.databinding.ListGeneresBinding
import com.example.movieapp.databinding.ListMoviesBinding
import com.example.movieapp.model.actors.Cast
import com.example.movieapp.utils.Constants
import javax.inject.Inject

class ActorsAdapter @Inject constructor() : RecyclerView.Adapter<ActorsAdapter.ActorsViewHolder>() {

    inner class ActorsViewHolder(val binding: ListActorsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)   // why this line?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorsViewHolder {
        return ActorsViewHolder(ListActorsBinding.inflate(LayoutInflater.from(parent.context)))

    }

    override fun onBindViewHolder(holder: ActorsViewHolder, position: Int) {
        val movie = differ.currentList[position]
        Glide.with(holder.itemView.context)  // Use holder.itemView.context to obtain the context
            .load(Constants.POSTER_BASE_URL + movie.profile_path)
            .into(holder.binding.imgActor)

        holder.binding.tvActorName.text = movie.original_name
        holder.binding.tvCharacterName.text = movie.character
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}