package com.example.movieapp.ui.fragment.homeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private var currentPage = 1
    private var isLastPage = false
    private var isDataLoaded = false
    private var selectedGenreId: String? = null

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


        setupViews()
        observeData()
        loadData()
        setupListeners()

        viewModel.selectedGenre.observe(viewLifecycleOwner, Observer { genre ->
            if (genre != null) {
                selectedGenreId = genre.id.toString()
                binding.tvPopular.text = genre.name
                viewModel.loadMoviesByGenre(selectedGenreId!!)
            }
        })



        Log.i("baso", "onViewCreated: ")
    }



    override fun onResume() {
        super.onResume()
        selectedGenreId?.let { genreId ->
            viewModel.loadMoviesByGenre(genreId)
            // Update movieAdapter with loaded movies from genre
        }
    }
    override fun onPause() {
        super.onPause()
        Log.i("baso", "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i("baso", "onStop: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("baso", "onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("baso", "onDestroy: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("baso", "onDetach: ")
    }

    private fun setupViews() {
        binding.apply {
            rvGenre.apply {
                adapter = genreAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(SpaceItemDecoration(20))
            }
            rvMovie.apply {
                adapter = movieAdapter
                layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                addItemDecoration(SpaceItemDecoration(20))
            }
        }
    }

    private fun observeData() {
        viewModel.apply {
            popularMovieList.observe(viewLifecycleOwner, Observer { movies ->
                if (selectedGenreId.isNullOrEmpty()) {
                    // If no genre is selected, show popular movies
                    movieAdapter.differ.submitList(movies.results)
                }
            })
            genreMoviesList.observe(viewLifecycleOwner) { movies ->
                if (!selectedGenreId.isNullOrEmpty()) {
                    // If a genre is selected, show genre-based movies
                    movieAdapter.differ.submitList(movies.results)
                }
            }
            genreList.observe(viewLifecycleOwner, Observer { genres ->
                genreAdapter.differ.submitList(genres.genres)
            })
        }
    }

    private fun loadData() {
        if (!isDataLoaded) {
            viewModel.loadPopularMoviesList(currentPage)
            viewModel.loadGenreList()
            isDataLoaded = true
        } else {
            // Data is already loaded, submit the existing data to the adapters
            viewModel.popularMovieList.value?.let { movies ->
                movieAdapter.differ.submitList(movies.results)
            }
            viewModel.genreList.value?.let { genres ->
                genreAdapter.differ.submitList(genres.genres)
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            imgSearch.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
                findNavController().navigate(action)
            }
            movieAdapter.setOnItemClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailFragment(it.id)
                findNavController().navigate(action)
            }
            rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        currentPage++
                        if (selectedGenreId.isNullOrEmpty()) {
                            viewModel.loadPopularMoviesList(currentPage)
                        } else {
                            viewModel.loadMoviesByGenre(selectedGenreId!!)
                        }
                    }
                }
            })


        }

        genreAdapter.setOnItemClickListener { genre ->
            viewModel.setSelectedGenre(genre)
            viewModel.loadMoviesByGenre(genre.id.toString())
            binding.tvPopular.text = genre.name

        }
    }
}
