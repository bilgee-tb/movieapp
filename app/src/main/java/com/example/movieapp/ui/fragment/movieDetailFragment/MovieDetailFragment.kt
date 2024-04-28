package com.example.movieapp.ui.fragment.movieDetailFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.adapter.ActorsAdapter
import com.example.movieapp.databinding.FragmentMovieDetailBinding
import com.example.movieapp.db.MovieEntity
import com.example.movieapp.ui.SpaceItemDecoration
import com.example.movieapp.utils.Constants
import com.example.movieapp.viewmodel.ApiViewModel
import com.example.movieapp.viewmodel.DatabaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailBinding
    private var movieId = 0
    private val viewModel: ApiViewModel by viewModels()
    private val databaseViewModel: DatabaseViewModel by viewModels()
    @Inject
    lateinit var entity: MovieEntity

    @Inject
    lateinit var actorsAdapter: ActorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Extract movieId from arguments
        arguments?.let {
            movieId = it.getInt("movieId")
        }
        // Load movie details and credits if movieId is valid
        if (movieId > 0) {
            viewModel.loadMovieDetails(movieId)
            viewModel.loadCreditsMovie(movieId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         fun formatRuntime(runtime: Int): String {
            val hours = runtime / 60
            val minutes = runtime % 60
            return "${hours}h ${minutes}m"
        }

        binding.apply {
            viewModel.detailsMovie.observe(viewLifecycleOwner) { response ->
                val context = requireContext() // Obtain the Context
                Glide.with(context)
                    .load(Constants.POSTER_BASE_URL + response.poster_path)
                    .into(imgPoster)
                tvMovieName.text = response.title
                tvMovieRate.text = response.vote_average.toString()
                tvMovieTime.text = formatRuntime(response.runtime)
                tvMovieDate.text = response.release_date
                tvSummaryInfo.text = response.overview
                //set img fav click
                favImg.setOnClickListener {
                    entity.id = movieId
                    entity.posterPath = response.poster_path
                    entity.title = response.title
                    entity.voteAverage = response.vote_average.toString()
                    entity.originalLanguage=response.original_language
                    entity.releaseDate = response.release_date
                    entity.runTime=formatRuntime(response.runtime)
                    databaseViewModel.favoriteMovie(movieId, entity)
                }

            }


            viewModel.creditsMovie.observe(viewLifecycleOwner) { movies ->
                actorsAdapter.differ.submitList(movies.cast)

                binding.rvActors.apply {
                    adapter = actorsAdapter
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    addItemDecoration(SpaceItemDecoration(20)) // Add 20dp of spacing
                }
            }


            // Set click listener for back button
            backImg.setOnClickListener {
                findNavController().navigateUp()
            }
//
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    if (databaseViewModel.existMovie(movieId)) {
                        favImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.scarlet))
                    } else {
                        favImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                }
            }

            databaseViewModel.isFavorite.observe(viewLifecycleOwner) {
                if (it) {
                    favImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.scarlet))
                } else {
                    favImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
                }
            }
        }
    }


}