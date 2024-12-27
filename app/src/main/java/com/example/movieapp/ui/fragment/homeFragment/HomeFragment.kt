package com.example.movieapp.ui.fragment.homeFragment

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.adapter.GenerMovieAdapter
import com.example.movieapp.adapter.PopularMovieAdapter
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.ui.SpaceItemDecoration
import com.example.movieapp.utils.NetworkChangeReceiver
import com.example.movieapp.viewmodel.ApiViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), NetworkChangeReceiver.NetworkChangeListener{
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: ApiViewModel by viewModels()
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkChangeReceiver = NetworkChangeReceiver(this)
        networkChangeReceiver.register(requireContext())

        checkInternetConnectionAndChangeLayout()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkChangeReceiver.unregister()
    }

    override fun onNetworkChanged(isConnected: Boolean) {
        requireActivity().runOnUiThread {
            if (isConnected) {
                binding.animationView.visibility = View.GONE
                binding.internetButton.visibility = View.GONE
                binding.tvPopular.visibility = View.VISIBLE
                binding.imgSearch.visibility = View.VISIBLE
                initializeHomeFragment()
            } else {
                binding.animationView.visibility = View.VISIBLE
                binding.internetButton.visibility = View.VISIBLE
                binding.tvPopular.visibility = View.GONE
                binding.imgSearch.visibility = View.GONE
                binding.rvGenre.visibility = View.GONE
                binding.rvMovie.visibility = View.GONE
                initializeNoInternetLayout()
            }
        }
    }
    private fun checkInternetConnectionAndChangeLayout() {
        if (isInternetAvailable(requireContext())) {
            initializeHomeFragment()
        } else {
            // No internet, show no internet layout
            binding.animationView.visibility = View.VISIBLE
            binding.internetButton.visibility = View.VISIBLE
            binding.tvPopular.visibility = View.GONE
            binding.imgSearch.visibility = View.GONE
            initializeNoInternetLayout()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        checkInternetConnectionAndChangeLayout()
//    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    private fun initializeHomeFragment() {
        // Initialize views, observe data, load data, and setup listeners for home fragment
        setupViews()
        observeData()
        loadData()
        setupListeners()
    }

    private fun initializeNoInternetLayout() {
        // Setup listeners for retry button in no internet layout
        binding.internetButton.setOnClickListener {
            if (isInternetAvailable(requireContext())) {
                // Internet is available, switch to home fragment layout
                binding.animationView.visibility = View.GONE
                binding.internetButton.visibility = View.GONE
                initializeHomeFragment() // Reinitialize home fragment
            } else {
                // Still no internet, display a message or handle it as needed
                Toast.makeText(requireContext(), "Still no internet", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //    override fun onResume() {
//        super.onResume()
//        selectedGenreId?.let { genreId ->
//            viewModel.loadMoviesByGenre(genreId)
//            // Update movieAdapter with loaded movies from genre
//        }
//    }




    private fun setupViews() {
        binding.apply {
            rvGenre.apply {
                adapter = genreAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(SpaceItemDecoration(20))
            }
            rvMovie.apply {
                adapter = movieAdapter
                layoutManager =
                    GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                addItemDecoration(SpaceItemDecoration(20))
            }
        }
    }

    private fun observeData() {
        requireActivity().runOnUiThread {
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
