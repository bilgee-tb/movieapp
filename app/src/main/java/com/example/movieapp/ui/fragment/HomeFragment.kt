package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.adapter.GenerMovieAdapter
import com.example.movieapp.adapter.PopularMovieAdapter
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.ui.SpaceItemDecoration
import com.example.movieapp.viewmodel.ApiViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: ApiViewModel by viewModels()
    @Inject
    lateinit var movieAdapter: PopularMovieAdapter
    @Inject
    lateinit var genreAdapter: GenerMovieAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retain instance when fragment is paused
//        retainInstance = true
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preparePopularMovies()
        viewModel.loadPopularMoviesList()
        observePopularMovies()

        prepareGenreRecyclerView()
        viewModel.loadGenreList()
        observeGenres()

        // Observe genre movies list
        observeGenreByMovies()


        binding.imgSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        movieAdapter.setOnItemClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailFragment(it.id)
            findNavController().navigate(action)
        }

    }

    private fun observeGenreByMovies() {
        // Observe genre item click event
        genreAdapter.setOnItemClickListener { genre ->
            // Load movies by the selected genre
            viewModel.loadMoviesByGenre(genre.id.toString())
            binding.tvPopular.text=genre.name
        }

        viewModel.genreMoviesList.observe(viewLifecycleOwner) { movies ->
            movieAdapter.differ.submitList(movies.results)

            binding.rvMovie.apply {
                adapter = movieAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
        }


    }

    private fun observePopularMovies() {
        viewModel.popularMovieList.observe(viewLifecycleOwner, Observer { movies ->
            movieAdapter.differ.submitList(movies.results)
        })
    }

    private fun observeGenres() {
        viewModel.genreList.observe(viewLifecycleOwner, Observer { genres ->
            genreAdapter.differ.submitList(genres.genres)
        })
    }


    private fun prepareGenreRecyclerView() {
        binding.rvGenre.apply {
            adapter = genreAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(SpaceItemDecoration(20)) // Add 20dp of spacing

        }
    }

    private fun preparePopularMovies() {
        binding.rvMovie.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(SpaceItemDecoration(20)) // Add 20dp of spacing

        }
    }


}