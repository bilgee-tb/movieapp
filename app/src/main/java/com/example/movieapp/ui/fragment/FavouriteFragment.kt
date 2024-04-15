package com.example.movieapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.adapter.FavouriteMovieAdapter
import com.example.movieapp.databinding.FragmentFavouriteBinding
import com.example.movieapp.db.MovieEntity
import com.example.movieapp.ui.SpaceItemDecoration
import com.example.movieapp.viewmodel.DatabaseViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding

    @Inject
    lateinit var favoriteMoviesAdapter: FavouriteMovieAdapter
    private val databaseViewModel: DatabaseViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            databaseViewModel.loadFavoriteMovieList()
            databaseViewModel.favoriteMovieList.observe(viewLifecycleOwner) { newMovieList ->

                if (newMovieList != null) {
                    favoriteMoviesAdapter.setFavoriteMovieList(newMovieList)
                }


                binding.rvFavouriteMovie.apply {
                    adapter = favoriteMoviesAdapter
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    addItemDecoration(SpaceItemDecoration(20)) // Add 20dp of spacing
                }
            }

            favoriteMoviesAdapter.setOnItemClickListener {
                val direction =
                    FavouriteFragmentDirections.actionFavouriteFragmentToMovieDetailFragment(it.id)
                findNavController().navigate(direction)
            }

            databaseViewModel.emptyList.observe(viewLifecycleOwner) { isEmpty ->
                if (isEmpty) {
                    binding.emptyItemsLay.visibility = View.VISIBLE
                    binding.rvFavouriteMovie.visibility = View.INVISIBLE
                } else {
                    binding.emptyItemsLay.visibility = View.INVISIBLE
                    binding.rvFavouriteMovie.visibility = View.VISIBLE
                }
            }
        }


        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (position != RecyclerView.NO_POSITION && position < favoriteMoviesAdapter.favoriteMovie.size) {
                    val deletedMovie = favoriteMoviesAdapter.getMelaByPosition(position)
                    // Delete the movie from the database
                    databaseViewModel.deleteMovie(deletedMovie)

                    // Remove the deleted movie from the list and notify the adapter
                    val updatedList = ArrayList(favoriteMoviesAdapter.favoriteMovie)
                    updatedList.removeAt(position)
                    favoriteMoviesAdapter.favoriteMovie = updatedList
                    favoriteMoviesAdapter.notifyItemRemoved(position)

                    Snackbar.make(requireView(), "Movie deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo") {
                            // Insert the deleted movie back into its original position and notify the adapter
                            updatedList.add(position, deletedMovie)
                            favoriteMoviesAdapter.favoriteMovie = updatedList
                            favoriteMoviesAdapter.notifyItemInserted(position)

                            // Insert the deleted movie back into the database
                            databaseViewModel.insertMovie(deletedMovie)

                        }.show()
                }
            }


        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvFavouriteMovie)

    }

}