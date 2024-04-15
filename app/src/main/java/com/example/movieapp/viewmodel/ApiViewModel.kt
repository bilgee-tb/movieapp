package com.example.movieapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.actors.ActorsResponse
import com.example.movieapp.model.genere.GenreResponse
import com.example.movieapp.model.movieDetails.MovieDetailResponse
import com.example.movieapp.model.popularMovie.MovieResponse
import com.example.movieapp.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiViewModel @Inject constructor (private  val apiRepository: ApiRepository): ViewModel() {


    val genreList = MutableLiveData<GenreResponse>()
    val popularMovieList = MutableLiveData<MovieResponse>()
    val genreMoviesList = MutableLiveData<MovieResponse>()
    val searchMovieList = MutableLiveData<MovieResponse>()
    val creditsMovie = MutableLiveData<ActorsResponse>()
   val detailsMovie = MutableLiveData<MovieDetailResponse>()

    val loading = MutableLiveData<Boolean>()

    fun loadGenreList() = viewModelScope.launch {
        loading.postValue(true)
        try {
            val response = apiRepository.getGenres()
            if (response.isSuccessful) {
                genreList.postValue(response.body())
            } else {
                // Log error or handle error cases
                Log.e("ApiViewModel", "Error: ${response.message()}")
            }
        } catch (e: Exception) {
            // Log exception
            Log.e("ApiViewModel", "Exception: $e")
        }finally {
            loading.postValue(false)
        }
    }

   fun loadPopularMoviesList() = viewModelScope.launch {
       loading.postValue(true)
       try {
           val response = apiRepository.getPopularMovies(1)
           if (response.isSuccessful) {
               popularMovieList.postValue(response.body())
           } else {
               // Log error if response is not successful
               Log.e("ApiViewModel", "Error loading popular movies: ${response.message()}")
           }
       } catch (e: Exception) {
           // Log exception if an error occurs during the API call
           Log.e("ApiViewModel", "Exception loading popular movies: $e")
       } finally {
           loading.postValue(false)
       }
    }

    fun loadMoviesByGenre(with_genres :String) = viewModelScope.launch {
        loading.postValue(true)
        try {
            val response = apiRepository.getMovieByGenre(1, with_genres)
            if (response.isSuccessful) {
                genreMoviesList.postValue(response.body())
            } else {
                // Log error if response is not successful
                Log.e("ApiViewModel", "Error loading movies by genre: ${response.message()}")
            }
        } catch (e: Exception) {
            // Log exception if an error occurs during the API call
            Log.e("ApiViewModel", "Exception loading movies by genre: $e")
        } finally {
            loading.postValue(false)
        }
    }


    fun  loadSearchMovie(name:String)=viewModelScope.launch {
        loading.postValue(true)
        try {
            val response=apiRepository.getSearchMovielist(1,name)
            if (response.isSuccessful){
                searchMovieList.postValue(response.body())
            }else{
                Log.e("ApiViewModel", "Error loading searchMovie: ${response.message()}")
            }

        }catch (e:Exception){
            Log.e("ApiViewModel", "Exception loading search movie: $e")
        } finally {
            loading.postValue(false)
        }

    }

    fun loadCreditsMovie(id: Int) = viewModelScope.launch {
        loading.postValue(true)
        val response = apiRepository.getMovieCredits(id)
        if (response.isSuccessful) {
            creditsMovie.postValue(response.body())
        }
        loading.postValue(false)
    }


    fun loadMovieDetails(id: Int) = viewModelScope.launch {
        loading.postValue(true)
        val response = apiRepository.getMovieDetails(id)
        if (response.isSuccessful) {
            detailsMovie.postValue(response.body())
        }
        loading.postValue(false)
    }
}