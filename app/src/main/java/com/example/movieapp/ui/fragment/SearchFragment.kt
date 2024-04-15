package com.example.movieapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.adapter.PopularMovieAdapter
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.ui.SpaceItemDecoration
import com.example.movieapp.viewmodel.ApiViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: ApiViewModel by viewModels()

    @Inject
    lateinit var movieAdapter: PopularMovieAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareSearchMovie()
        observeSearchMovie()
        // Set click listener for imgSearchBack
        binding.imgSearchBack.setOnClickListener {
            // Navigate back to the home fragment
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun observeSearchMovie() {
        binding.idSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
            private var searchJob: Job? = null

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    newText?.let {
                        delay(500)
                        if (it.isEmpty()) {
                            // Clear the RecyclerView when the search query is empty
                            movieAdapter.differ.submitList(null)
                        } else {
                            // Perform the search and update the RecyclerView with the results
                            viewModel.loadSearchMovie(it)
                        }
                    }
                }
                return false
            }
        })

        // Observe searchMovieList to update the adapter with the new search results
        viewModel.searchMovieList.observe(viewLifecycleOwner) { movieResponse ->
            movieAdapter.differ.submitList(movieResponse.results)
        }

        // Observe searchMovieList to handle empty result case
        viewModel.searchMovieList.observe(viewLifecycleOwner) { movieResponse ->
            if (movieResponse.results.isEmpty()) {
                binding.emptyItemsLay.visibility = View.VISIBLE
            } else {
                binding.emptyItemsLay.visibility = View.GONE
            }
        }

        movieAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToMovieDetailFragment(it.id)
            findNavController().navigate(action)
        }
    }

    private fun prepareSearchMovie() {
        // Setup RecyclerView
        binding.rvSearchMovie.apply {
            movieAdapter = PopularMovieAdapter()
            adapter = movieAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(SpaceItemDecoration(20)) // Add 20dp of spacing

        }
    }
}





